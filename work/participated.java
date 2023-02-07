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
List<Award> targetAwards = awardService.getAwardList(param);

targetAwards 조회해서 setAwardDtoList(targetAwards)로 넘기기 
