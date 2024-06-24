package archit.springboot.booksocialnetwork.Dto;

import ch.qos.logback.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    private static final Logger logger= LoggerFactory.getLogger(FileUtil.class);

    public static byte[] readFileFromLocation(String bookCover) {
        if(StringUtil.isNullOrEmpty(bookCover))return null;
        try{
            Path path = new File(bookCover).toPath();
            return Files.readAllBytes(path);
        }
        catch (IOException e){
            logger.warn("Could not find any book cover in the specified book cover path"+e);
        }
        return new byte[0];
    }
}
