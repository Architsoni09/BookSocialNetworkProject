package archit.springboot.booksocialnetwork.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${spring.file.upload.photos-output-path}")
    private String fileUploadPath;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public String saveFile(@NonNull MultipartFile sourceFile,@NonNull Long userId) {
        final String fileUploadSubPath="users"+ File.separator+userId;

        return uploadFile(sourceFile,fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile,@NonNull  String fileUploadSubPath) {
        final String finalUploadPath=fileUploadPath+ File.separator+ fileUploadSubPath;
        File targetFolder=new File(finalUploadPath);
        if (!targetFolder.exists()){
            boolean folderCreated=targetFolder.mkdir();
            if(!folderCreated) logger.warn("Could not create TargetFolder");
            return null;
        }
        final String fileExtension=getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath=finalUploadPath+File.separator+System.currentTimeMillis()+"."+fileExtension;
        Path path= Paths.get(targetFilePath);
        try{
            Files.write(path,sourceFile.getBytes());
            logger.info("File was successfully uploaded");
        }
        catch (IOException e){
            logger.error("File was not saved"+ e);
        }
        return targetFilePath;
    }

    private String getFileExtension(String originalFilename) {
        if(originalFilename.isEmpty()) return null;
        int lastDotIndex=originalFilename.lastIndexOf('.');
        if(lastDotIndex!=-1) return originalFilename.substring(lastDotIndex+1).toLowerCase();
        else return "";
    }
}
