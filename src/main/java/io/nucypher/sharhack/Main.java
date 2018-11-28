package io.nucypher.sharhack;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.uploadcare.api.Client;
import com.uploadcare.api.Project;
import com.uploadcare.upload.FileUploader;
import com.uploadcare.upload.UploadFailureException;
import com.uploadcare.upload.Uploader;
import com.uploadcare.urls.CdnPathBuilder;
import com.uploadcare.urls.Urls;
import org.encryptor4j.util.FileEncryptor;

import java.io.*;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException{
        /*Client client = new Client("publickey", "privatekey");
        Project project = client.getProject();
        Project.Collaborator owner = project.getOwner();

        List<URI> published = new ArrayList<URI>();
        Iterable<File> files = client.getFiles().asIterable();
        for (File file : files) {
            if (file.isStored()){
                published.add(file.getOriginalFileUrl());
            }
        }
        File file = client.getFile("85b5644f-e692-4855-9db0-8c5a83096e25");
        CdnPathBuilder builder = file.cdnPath()
                .resizeWidth(200)
                .cropCenter(200, 200)
                .grayscale();
        URI url = Urls.cdn(builder);

        Client client1 = Client.demoClient();
        java.io.File file1 = new java.io.File("olympia.jpg");
        Uploader uploader = new FileUploader(client1, file1);
        try {
            File file2 = uploader.upload().save();
            System.out.println(file2.getOriginalFileUrl());
        } catch (UploadFailureException e) {
            System.out.println("Upload failed :(");
        }*/
        String clientRegion = "USEast(Ohio)";
        String bucketName = "nucypher-sharhack";
        AWSCredentials credentials = new BasicAWSCredentials(
                "***********",
                "****************"
        );

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.US_EAST_2)
                    .build();

            if (s3Client.doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                //s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
               /* File srcFile = new File("C:/Users/a.sharafutdinov/Pictures/1.txt");
                File destFile = new File("C:/Users/a.sharafutdinov/Pictures/1.txt.encrypted");
                String password = "mysupersecretpassword";
                FileEncryptor fe = new FileEncryptor(password);
                fe.encrypt(srcFile, destFile);
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Bucket location: " + bucketLocation);
                //s3Client.putObject(bucketName, "Document/1.txt", destFile);
                //s3Client.putObject(bucketName, "Document/1.txt", new File("C:/Users/a.sharafutdinov/Pictures/1.txt"));

                System.out.println(s3Client);*/



                System.out.println("Downloading an object");
                fullObject = s3Client.getObject(new GetObjectRequest(bucketName, s3Client.getObject(bucketName, "Document/1.txt").getKey()));
                System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
                System.out.println("Content: ");
                displayTextInputStream(fullObject.getObjectContent());

                // Get a range of bytes from an object and print the bytes.
                GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, s3Client.getObject(bucketName, "Document/1.txt").getKey())
                        .withRange(0,9);
                objectPortion = s3Client.getObject(rangeObjectRequest);
                displayTextInputStream(objectPortion.getObjectContent());
                System.out.println("Printing bytes retrieved.");


                // Get an entire object, overriding the specified response headers, and print the object's content.
                ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides()
                        .withCacheControl("No-cache")
                        .withContentDisposition("attachment; filename=example.txt");
                GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, s3Client.getObject(bucketName, "Document/1.txt").getKey())
                        .withResponseHeaders(headerOverrides);
                headerOverrideObject = s3Client.getObject(getObjectRequestHeaderOverride);
                displayTextInputStream(headerOverrideObject.getObjectContent());
                System.out.println("Printing bytes retrieved.");
                File srcFile1 = new File("C:/Users/a.sharafutdinov/Pictures/2.txt.encrypted");
                File destFile1 = new File("C:/Users/a.sharafutdinov/Pictures/2.txt");
//                final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
                try {
                    S3Object o = s3Client.getObject(bucketName, s3Client.getObject(bucketName, "Document/1.txt").getKey());
                    S3ObjectInputStream s3is = o.getObjectContent();
                    FileOutputStream fos = new FileOutputStream(srcFile1);
                    byte[] read_buf = new byte[1024];
                    int read_len = 0;
                    while ((read_len = s3is.read(read_buf)) > 0) {
                        fos.write(read_buf, 0, read_len);
                    }
                    String password1 = "mysupersecretpassword";
                    FileEncryptor fe1 = new FileEncryptor(password1);
                    fe1.decrypt(srcFile1, destFile1);
                    s3is.close();
                    fos.close();
                } catch (AmazonServiceException e) {
                    System.err.println(e.getErrorMessage());
                    System.exit(1);
                } catch (FileNotFoundException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }


            }

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if(fullObject != null) {
                fullObject.close();
            }
            if(objectPortion != null) {
                objectPortion.close();
            }
            if(headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }

    }
    private static void displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
    }
}
