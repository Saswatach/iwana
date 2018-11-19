//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.evernote.iwana.extract;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

public class TestExtractTextIWAParser {
    public TestExtractTextIWAParser() {
    }

    @Test
    public void test() throws Exception {
        String[] args = new String[]{Paths.get(this.getClass().getResource("/test-documents/testPages2013.pages").toURI()).toString()};
        ExtractTextApp extractTextApp1 = new ExtractTextApp();
        System.out.println(extractTextApp1.parse(args));
        System.out.println(".............................................");
        String[] args1 = new String[]{Paths.get(this.getClass().getResource("/test-documents/testKeynote2013.key").toURI()).toString()};
        ExtractTextApp extractTextApp2 = new ExtractTextApp();
        System.out.println(extractTextApp2.parse(args1));
        System.out.println(".............................................");
        String[] args2 = new String[]{Paths.get(this.getClass().getResource("/test-documents/testNumbers2013.numbers").toURI()).toString()};
        ExtractTextApp extractTextApp3 = new ExtractTextApp();
        System.out.println(extractTextApp3.parse(args2));
        System.out.println(".............................................");
    }

    private void assertContains(String needle, String haystack) {
        int i = haystack.indexOf(needle);
        if (i < 0) {
            Assert.fail("Couldn't find >" + needle + "< in >" + haystack + "<");
        }

    }

    private File getTestFile(String testFileName) throws URISyntaxException {
        return Paths.get(this.getClass().getResource("/test-documents/" + testFileName).toURI()).toFile();
    }

    private static final class SimpleExtractTextCallback extends ExtractTextCallback {
        StringBuilder sb;

        private SimpleExtractTextCallback() {
            this.sb = new StringBuilder();
        }

        public void onTextBlock(String text, TextAttributes scope) {
            this.sb.append(text);
            this.sb.append("\n");
        }

        public String toString() {
            return this.sb.toString();
        }

        public void onMetaBlock(String key, String value) {
            this.sb.append("\n");
        }
    }
}
