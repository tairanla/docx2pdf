package dev.tairanla;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.docx4j.Docx4J;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;

@RestController
public class UploadController {

    @PostMapping(value = "/convert",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "文件转换接口(docx -> pdf)", description = "上传 Word 文档并转换为 PDF")
    public ResponseEntity<byte[]> uploadAndConvert(@RequestParam("file") MultipartFile file) throws Exception {
        InputStream is = new ByteArrayInputStream(file.getBytes());

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);

        // 在内存中将所有字体都改成宋体
        // 遍历所有段落
        List<Object> texts = wordMLPackage.getMainDocumentPart().getJAXBNodesViaXPath("//w:r", true);
        for (Object obj : texts) {
            if (obj instanceof R run) {
                RPr rPr = run.getRPr();
                if (rPr == null) {
                    rPr = new RPr();
                    run.setRPr(rPr);
                }
                // 设置字体
                RFonts rf = new RFonts();
                rf.setAscii("SimSun");      // 西文
                rf.setHAnsi("SimSun");      // 英文和数字
                rf.setEastAsia("宋体");      // 中文
                rPr.setRFonts(rf);
            }
        }

        // Set up font mapper (optional)
        // Mapper fontMapper = new IdentityPlusMapper();  // Only for Windows, unless you have Microsoft's fonts installed
        Mapper fontMapper = new BestMatchingMapper();  // Good for Linux (and OSX?)
        wordMLPackage.setFontMapper(fontMapper);

        ByteArrayOutputStream os= new ByteArrayOutputStream();
        Docx4J.toPDF(wordMLPackage, os);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", UUID.randomUUID().toString() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(os.toByteArray());
    }
}
