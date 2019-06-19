package org.superbiz.moviefun.blobstore;

import org.apache.tika.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        File targetFile = new File(blob.getName());

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        InputStream uploadedFile = blob.getInputStream();

        byte[] imageBytes = new byte[uploadedFile.available()];
        uploadedFile.read(imageBytes);

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(imageBytes);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

       File imageFile = new File(name);
       if (!imageFile.exists()) {
           try {
               imageFile = new File(getSystemResource("default-cover.jpg").toURI());
               name=getSystemResource("default-cover.jpg").getPath();
           } catch (URISyntaxException e) {
               e.printStackTrace();
           }
       }

       InputStream image = new FileInputStream(imageFile);

       Blob blob = new Blob(name, image, "image");

       return Optional.of(blob);
    }

    @Override
    public void deleteAll() {
        // ...
    }

}