/*
* param: planDisplaySn, memberSn
* - 위에 파라미터를 가지고 쿼리를 조회해서 award형태로 만든다. 
* - 그리고 award로 기존에 있는걸로 사용해서 mapper에 담아줌 
*/ 
@PostMapping("plandisplays/{planDisplaySn}/eventParticipatedResult")
public ResponseEntity<?> checkEventParticipatedPost(
    @PathVariable Long planDisplaySn, //기획전시 일련번호
    ResponseMapper mapper) {
    
    // memberSn 가져오기 
    Long memberSn = RestApiRequestContext.getCurrentMemberSn();
    logger.debug(">>> 행사참여 : 댓글등록 memberSn : {}", memberSn);
    if (memberSn == null) {
        throw new BadRequestException("EAPI004", "Context 회원일련번호 오류. (null)");
    }

    Member member = checkMember(memberSn); 

    //makeWinner()
    
}

// planDisplaySn, memberSn 
private void checkWinner(planDisplay planDisplay, Member member){

}

/*### 정리 
    1. 결국 랜덤으로 돌린 2328아이들이 들어왔고, 
    2. setAwardDtoList(newDefaultAwards); 에서 호출을 한다. 
    3. awards에서 조회한 아이들을 가지고... set해준다음에
    4. 조건문에 따라 분기처리를 해줘서 상세값을 가져오고 
    5. DTO로 꽂아서 돌려보낸다. */

SearchParam param = SearchParamBuilder.create().build();
param.addAndFilters(FilterFactory.equal("mallId", MallIdChecker.getCurrentMallId()));
param.addAndFilters(FilterFactory.equal("awardProgramCode", (Comparable<?>) AwardProgram.PlanDisplay));
param.addAndFilters(FilterFactory.equal("awardProgramSn", (Comparable<?>) planDisplay.getPlanDisplaySn()));
param.addAndFilters(FilterFactory.equal("lang.langCode", (Comparable<?>) LocaleUtils.getCurrentLangCode()));
List<Award> targetAwards = awardService.getAwardListResult(param);

targetAwards 조회해서 setAwardDtoList(targetAwards)로 넘기기 

//getWinnerAwards
@Override
public List<WinnerAward> getWinnerAwards(SearchParam param) {
    QWinner winner = QWinner.winner;
    QWinnerAward q = QWinnerAward.winnerAward;

    BWJPAQuery<WinnerAward> query = (BWJPAQuery<WinnerAward>) factory.query();

    query.select(q)
            .from(q)
            .leftJoin(winner).on(q.winner.winnerSn.eq(winner.winnerSn))
            .searchParam(param)
            .subsetStartsWith("winner.").setRoot(winner)
            .apply();

    return query.fetch();
}

//getAwardList
@Override
public List<Award> getAwardList(SearchParam param) {

    QAward q = QAward.award;
    QAwardLang lang = QAwardLang.awardLang;

    BWJPAQuery<Award> query = this.factory
            .select(q)
            .from(q)
            .innerJoin(q.langs, lang).fetchJoin()
            .searchParam(param)
            .subsetStartsWith("lang.").setRoot(lang)
            .apply();

    return query.fetch();
}


 // 증정 미처리 재처리 배치 대상 조회
    @Override
    public List<BTuple> getAwardReHandlingBatchTargetListWithMember(SearchParam searchParam) {

        QAward award = QAward.award;
        QWinnerAward winnerAward = QWinnerAward.winnerAward;
        QWinner winner = QWinner.winner;
        QMember member = QMember.member;

        @SuppressWarnings("unchecked")
        BWJPAQuery<Tuple> query = (BWJPAQuery<Tuple>) factory.query();
        query.select(award, winner, winnerAward, member)
                .from(award)
                .innerJoin(winnerAward).on(winnerAward.award.awardSn.eq(award.awardSn))
                .innerJoin(winner).on(winner.winnerSn.eq(winnerAward.winner.winnerSn))
                .join(member).on(member.memberSn.eq(winner.memberSn))
                .searchParam(searchParam)
                .subsetStartsWith("member.").setRoot(member)
                .subsetStartsWith("winnerAward.").setRoot(winnerAward)
                .subsetStartsWith("award.").setRoot(award)
                .subsetStartsWith("winner.").setRoot(winner)
                .apply();
        query.orderBy(winner.participantSn.asc());  // 참여자 일련번호 기준으로 정렬 후 Loop 수행

        List<Tuple> tupleList = query.fetch();

        //변환
        List<BTuple> list = new ArrayList<>();

        for (Tuple t : tupleList) {

            Map<String, Object> map = new HashMap<>();
            map.put("award", t.get(0, Award.class));
            t.get(0, Award.class).getLangs().size();
            map.put("winner", t.get(1, Winner.class));
            t.get(1, Winner.class).getLangs().size();
            map.put("winnerAward", t.get(2, WinnerAward.class));
            t.get(2, WinnerAward.class).getLangs().size();
            map.put("member", t.get(3, Member.class));

            MapTuple mapTuple = new MapTuple(map);
            list.add(mapTuple);
        }

        return list;
    }


     List<AwardExDTO> awardDtos = setAwardDtoList(newDefaultAwards);
     mapper.mapFrom("awards", awardDtos);



  #기획전/행사 - 참여 (댓글등록 : Base64)
  /v1/{mallId}/sales/plandisplays/{planDisplaySn}/eventParticipated:
    post:
      tags: [planDisplay]
      summary: 참여댓글형 행사참여 - 이미지 포함
      operationId: eventParticipatedPost
      description: 참여댓글형 행사참여 - 이미지 포함
      parameters:
        - $ref: '#/parameters/mallIdParam'
        - name: planDisplaySn
          type: integer
          format: int64
          in: path
          required: true
          description: 기획전시일련번호
        - name: body
          in: body
          description: 행사참여정보
                          , 댓글 - participantComment
                          , 댓글제목 - participantCommentTitle
                          , 약관동의여부 - termsAgreeYn - FO에서 판단 (상품/사은품)
                          , 이미지 일련번호
                          , 첨부파일 (Base64)
          schema:
            $ref: '#/definitions/EventParticipatedPost'
      responses:
        200:
          description: Success
          schema:
            $ref: '#/definitions/Awards'
            description: 행사 증정목록
                            , 당첨 - 당첨된 증정품목록
                            , 참여
        400:
          description: Invalid Parameter
      security:
        - ecp_fo: []
        - ecp_member: []

  #행사참여여부 확인
  /v1/{mallId}/sales/plandisplays/{planDisplaySn}/eventParticipatedYn:
    get:
      tags: [planDisplayEvent]
      summary: 행사참여여부 확인
      operationId: getEventParticipatedYn
      description: 행사참여여부 확인
      parameters:
        - $ref: '#/parameters/mallIdParam'
        - name: planDisplaySn
          type: integer
          format: int64
          in: path
          required: true
          description: 기획전시일련번호
      responses:
        200:
          description: Success
          schema:
            type: array
            items:
              $ref: '#/definitions/EventParticipatedYnResult'
            description: 행사참여정보
        400:
          description: Invalid Parameter




                tags: [planDisplay]
      summary: 증정품대상자 - 증정대상목록
      operationId: checkEventParticipatedPost
      description: 증정품대상자 - 증정대상목록
      parameters:
        - $ref: '#/parameters/mallIdParam'
        - name: planDisplaySn
          type: integer
          format: int64
          in: path
          required: true
          description: 기획전시일련번호
       

============================== 
* 오류메세지 
Caused by: org.hibernate.hql.internal.ast.QuerySyntaxException: Invalid path: 'winnerAward.winner.winnerSn' [select award


.innerJoin(winner).on(winner.winnerSn.eq(winnerAward.winner.winnerSn))

* 오류메세지 
org.hibernate.hql.internal.ast.InvalidPathException: Invalid path: 'winnerAward.winner.winnerSn'

SearchParam param = SearchParamBuilder.create().build();
param.addAndFilters(FilterFactory.equal("mallId", MallIdChecker.getCurrentMallId()));
param.addAndFilters(FilterFactory.equal("awardProgramCode", (Comparable<?>) AwardProgram.PlanDisplay));
param.addAndFilters(FilterFactory.equal("awardProgramSn", (Comparable<?>) planDisplaySn));
param.addAndFilters(FilterFactory.equal("lang.langCode", (Comparable<?>) LocaleUtils.getCurrentLangCode()));
param.addAndFilters(FilterFactory.equal("winner.memberSn", memberSn));

.where(mp.mallId.eq(mallId))