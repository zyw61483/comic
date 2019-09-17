function decrypt20180904(chapterImages) {
    var key = CryptoJS.enc.Utf8.parse("123456781234567G");  //十六位字符作为密钥
    var iv = CryptoJS.enc.Utf8.parse('ABCDEF1G34123412');
    var decrypt = CryptoJS.AES.decrypt(chapterImages,key, { iv: iv, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 });
//    console.log(decrypt);
    var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
    chapterImages = JSON.parse(decryptedStr.toString());
    return chapterImages;
//    ;SinMH.initChapter(chapterId,chapterName,comicId,comicName);
//    SinTheme.initChapter(chapterId,chapterName,comicId,comicName);
}