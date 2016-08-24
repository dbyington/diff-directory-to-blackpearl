package com.spectra.v2bpverify;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.spectralogic.ds3client.Ds3Client;
import com.spectralogic.ds3client.Ds3ClientBuilder;
import com.spectralogic.ds3client.helpers.Ds3ClientHelpers;
import com.spectralogic.ds3client.models.Contents;
import com.spectralogic.ds3client.models.SystemFailure;
import com.spectralogic.ds3client.models.bulk.Ds3Object;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class Main {
    public static void main(final String[] args) throws IOException {

        final Ds3Client ds3Client = Ds3ClientBuilder.fromEnv().withHttps(false).build();
        final Ds3ClientHelpers clientHelpers = Ds3ClientHelpers.wrap(ds3Client);

        System.out.println("Getting local file list");
        final Iterable<Ds3Object> localFiles = clientHelpers.listObjectsForDirectory(Paths.get(args[0]));

        System.out.println("Getting black pearl file list");
        final Iterable<Contents> bpfilelist = clientHelpers.listObjects(args[1]);
        final Iterable<Ds3Object> bpDs3Objects = clientHelpers.toDs3Iterable(bpfilelist);

        final ImmutableSet<Ds3Object> localFileSet = ImmutableSet.copyOf(localFiles);

        final ImmutableSet<Ds3Object> bpFileSet = ImmutableSet.copyOf(bpDs3Objects);

        System.out.println("Comparing...");
        final Sets.SetView<Ds3Object> difference = Sets.difference(localFileSet, bpFileSet);

        if (difference.isEmpty()) {
            System.out.println("No differences");
        } else {
            System.out.println("Found differences:");
            difference.forEach(ds3Object -> System.out.println("File: " + ds3Object.getName()));
        }


    }
}
