package com.github.iitdevelopment.delegator.messageReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MessageReader {
    private static List<byte[]> split(byte[] array, byte[] delimiter) {
        List<byte[]> byteArrays = new LinkedList<>();
        if (delimiter.length == 0) {
            return byteArrays;
        }
        int begin = 0;

        outer:
        for (int i = 0; i < array.length - delimiter.length + 1; i++) {
            for (int j = 0; j < delimiter.length; j++) {
                if (array[i + j] != delimiter[j]) {
                    continue outer;
                }
            }

            if (begin != i)
                byteArrays.add(Arrays.copyOfRange(array, begin, i));
            begin = i + delimiter.length;
        }

        if (begin < array.length)
            byteArrays.add(Arrays.copyOfRange(array, begin, array.length));

        return byteArrays;
    }

    public static List<byte[]> read(InputStream inputStream) throws IOException {
        byte[] data = new byte[65536];
        inputStream.read(data);
        List<byte[]> splitMessage = split(data, "\r\n\r\n".getBytes());

        if (splitMessage.size() == 1) {
            splitMessage.add(new byte[]{});
        }

        return splitMessage;
    }
}
