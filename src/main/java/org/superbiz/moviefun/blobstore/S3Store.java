package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;
    private String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        if (!this.s3Client.doesBucketExist(this.photoStorageBucket)) {
            this.s3Client.createBucket(this.photoStorageBucket);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.getContentType());
        this.s3Client.putObject(this.photoStorageBucket, blob.getName(), blob.getInputStream(), metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        S3Object s3cover = this.s3Client.getObject(this.photoStorageBucket, name);
        Blob blob = new Blob(name, s3cover.getObjectContent(), s3cover.getObjectMetadata().getContentType());
        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }
}
