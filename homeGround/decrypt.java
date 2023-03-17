1. upload버튼을 클릭하면 업로드할 엑셀을 복호화한다. 
    - decryptFile

public void handleNativeUploadSucceeded(Upload.SucceededEvent event) {
    if(ManualBenefit.class.equals(memberTgtUpperClass.getClass())) {
        try {
            collectCaptions(excelFile); 

            decryptFile(excelFile); 
        } catch {
            logger.Error("###복호화 에러")
        }
    }
}


= = = = = = =

public void decryptFile(File file) throws Exception {
    byte[] inputArr = FileUtils.redFileToByteArray(file); 
    Stirng inputBase64 = Base64.getEncoder().encodeToString(inputArr); 


}
