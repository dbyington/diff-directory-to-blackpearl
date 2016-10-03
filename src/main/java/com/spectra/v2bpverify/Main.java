package com.spectra.v2bpverify;

import com.spectralogic.ds3client.Ds3Client;
import com.spectralogic.ds3client.Ds3ClientBuilder;
import com.spectralogic.ds3client.helpers.Ds3ClientHelpers;
import com.spectralogic.ds3client.models.Contents;
import com.spectralogic.ds3client.models.bulk.Ds3Object;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(final String[] args) throws IOException {

        final String excludeString = "/.snapshot/";

        final Ds3Client ds3Client = Ds3ClientBuilder.fromEnv().withHttps(false).build();
        final Ds3ClientHelpers clientHelpers = Ds3ClientHelpers.wrap(ds3Client);

        System.out.println("Getting local file list");
        final Iterable<Ds3Object> localFiles = clientHelpers.listObjectsForDirectory(Paths.get(args[0]));

        System.out.println("Converting file list and stripping matches to: '" + excludeString + "'");
        final Set<Ds3Object> localSet = convertToSet(localFiles, excludeString);

        System.out.println("Getting black pearl file list");
        final Iterable<Contents> bpfilelist = clientHelpers.listObjects(args[1]);
        final Iterable<Ds3Object> bpDs3Objects = clientHelpers.toDs3Iterable(bpfilelist);

        System.out.println("Comparing...");
        difference(localSet, bpDs3Objects);

        if (localSet.isEmpty()) {
            System.out.println("No differences");
        } else {
            System.out.println("Found differences:");
            localSet.forEach(ds3Object -> System.out.println("Object: " + System.getenv("DS3_ENDPOINT") + "://" + args[1] + "/" + ds3Object.getName().replace("\\", "/") + " does not exist."));
        }
    }

    private static void difference(final Set<Ds3Object> localSet, final Iterable<Ds3Object> bpDs3Objects) {
        bpDs3Objects.forEach(localSet::remove);
    }

    private static Set<Ds3Object> convertToSet(final Iterable<Ds3Object> localFiles, final String exclude) {
        final Set<Ds3Object> returnSet = new HashSet<>();

        //strip the exclude pattern in the process of converting.
        localFiles.forEach(localFile->{
            if(!localFile.getName().contains(exclude)) {
                returnSet.add(localFile);
            }
        });

        return returnSet;
    }

}
