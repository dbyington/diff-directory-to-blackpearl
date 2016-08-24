package com.spectra.v2bpverify;

import com.spectralogic.ds3client.Ds3Client;
import com.spectralogic.ds3client.Ds3ClientBuilder;
import com.spectralogic.ds3client.helpers.Ds3ClientHelpers;
import com.spectralogic.ds3client.models.Contents;
import com.spectralogic.ds3client.models.bulk.Ds3Object;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(final String[] args) throws IOException {

        final Ds3Client ds3Client = Ds3ClientBuilder.fromEnv().withHttps(false).build();
        final Ds3ClientHelpers clientHelpers = Ds3ClientHelpers.wrap(ds3Client);

        System.out.println("Getting local file list");
        final Iterable<Ds3Object> localFiles = clientHelpers.listObjectsForDirectory(Paths.get(args[0]));

        System.out.println("Getting black pearl file list");
        final Iterable<Contents> bpfilelist = clientHelpers.listObjects(args[1]);
        final Iterable<Ds3Object> bpDs3Objects = clientHelpers.toDs3Iterable(bpfilelist);

        final Set<Ds3Object> localSet = convertToSet(localFiles);

        System.out.println("Comparing...");
        difference(localSet, bpDs3Objects);

        if (localSet.isEmpty()) {
            System.out.println("No differences");
        } else {
            System.out.println("Found differences:");
            localSet.forEach(ds3Object -> System.out.println("File: " + ds3Object.getName()));
        }
    }

    private static void difference(final Set<Ds3Object> localSet, final Iterable<Ds3Object> bpDs3Objects) {
        bpDs3Objects.forEach(localSet::remove);
    }

    private static Set<Ds3Object> convertToSet(final Iterable<Ds3Object> localFiles) {
        final Set<Ds3Object> returnSet = new HashSet<>();

        localFiles.forEach(returnSet::add);

        return returnSet;
    }
}
