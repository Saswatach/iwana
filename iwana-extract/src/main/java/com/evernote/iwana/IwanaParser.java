/**
 * Copyright 2014-2016 Evernote Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.evernote.iwana;

import com.evernote.iwana.pb.TSP.TSPArchiveMessages.ArchiveInfo;
import com.evernote.iwana.pb.TSP.TSPArchiveMessages.MessageInfo;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class IwanaParser<T extends IwanaParserCallback> {
    ArrayList<Integer> keyActions = new ArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 123, 124, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 10011));
    ArrayList<Integer> pageActions = new ArrayList(Arrays.asList(7, 10000, 10001, 10010, 10011, 10012, 10015, 10101, 10102, 10108, 10109, 10110, 10111, 10112, 10113, 10114, 10115, 10116, 10117, 10118, 10119, 10120, 10121, 10125, 10126, 10127, 10128, 10130, 10131, 10132, 10133, 10134, 10140, 10141, 10142, 10143, 10147, 10148, 10149, 10150, 10151, 10152, 10153, 10154, 10155, 10156, 10157));
    ArrayList<Integer> numActions = new ArrayList(Arrays.asList(1, 2, 3, 7, 10011, 12002, 12003, 12004, 12005, 12006, 12008, 12009, 12010, 12011, 12012, 12013, 12014, 12015, 12016, 12017, 12018, 12019, 12021, 12024, 12025, 12026, 12027, 12028, 12030));
    public String type = "";
    private String path = "";

    public IwanaParser() {
    }

    public void parse(File iworkFile, T target, String paths) throws IOException {
        target.onBeginDocument();
        this.path = paths;

        try {
            if (iworkFile.isDirectory()) {
                this.parseDirectory(iworkFile, target);
            } else {
               
                try {
                    FileInputStream fin = new FileInputStream(iworkFile);

                    try {
                        this.parseInternal(fin, target);
                    } finally {
                        if (fin != null) {
                            fin.close();
                        }

                    }
                } catch (Exception e) {
                   
                }
            }
        } catch (Exception e){
            throw new IOException (e);

        }
        finally {
            target.onEndDocument();
        }
    }

    

    private void parseDirectory(File dir, T target) throws IOException {
        IwanaContext<T> context = this.newContext(dir.getName(), target);
        File indexZip = new File(dir, "Index.zip");
        if (!indexZip.isFile()) {
            throw new FileNotFoundException("Could not find Index.zip: " + indexZip);
        } else {

            try {
                FileInputStream in = new FileInputStream(indexZip);

                try {
                    this.parseIndexZip(in, context, target, in);
                } finally {
                    if (in != null) {
                        in.close();
                    }

                }

            } catch (Exception e ) {
                throw new IOException (e);
            }
        }
    }

    public void parse(InputStream zipIn, T target) throws IOException {
        target.onBeginDocument();

        try {
            this.parseInternal(zipIn, target);
        } finally {
            target.onEndDocument();
        }

    }

    private void parseInternal(InputStream zipIn, T target) throws IOException {
        IwanaContext<T> context = null;
        boolean hasIndexDir = false;
        Throwable var5 = null;
        Object var6 = null;

        try {
            ZipInputStream zis = new ZipInputStream(zipIn);

            try {
                ZipEntry entry;
                while((entry = zis.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (context == null && name.endsWith("/Index.zip") && !entry.isDirectory()) {
                        int iSlash = name.indexOf(47);
                        int iIndex = name.indexOf("/Index.zip");
                        if (iSlash == iIndex) {
                            context = this.newContext(name.substring(0, iSlash), target);
                            this.parseIndexZip(zis, context, target, zipIn);
                            break;
                        }
                    } else if (name.startsWith("Index/") && !entry.isDirectory()) {
                        if (context == null) {
                            context = this.newContext("yoo." + this.type, target);
                            context.onBeginParseIndexZip();
                            hasIndexDir = true;
                        }

                        this.parseIndexZipEntry(zis, entry, context, target, zipIn);
                    }
                }

                if (context == null) {
                    throw new IOException("Could not find Index.zip archive");
                }

                if (hasIndexDir) {
                    context.onEndParseIndexZip();
                }
            } finally {
                if (zis != null) {
                    zis.close();
                }

            }

        } catch (Exception e) {
            throw new IOException (e);

        }
    }

    private void parseIndexZip(InputStream indexZipIn, IwanaContext<T> context, T target, InputStream zipIn) throws IOException {

            try {
                ZipInputStream zis = new ZipInputStream(indexZipIn);

                try {
                    context.onBeginParseIndexZip();

                    ZipEntry entry;
                    boolean foundIWA;
                    for(foundIWA = false; (entry = zis.getNextEntry()) != null; foundIWA |= this.parseIndexZipEntry(zis, entry, context, target, zipIn)) {
                        ;
                    }

                    if (!foundIWA) {
                        throw new IOException("Index.zip does not contain any .iwa files");
                    }
                } finally {
                    if (zis != null) {
                        zis.close();
                    }

                }
            } catch (Throwable e) {
                throw new IOException (e);

            }finally {
            context.onEndParseIndexZip();
        }
    
    }

    private boolean parseIndexZipEntry(ZipInputStream zis, ZipEntry entry, IwanaContext<T> context, T target, InputStream zipIn) throws IOException {
        if (entry.isDirectory()) {
            return false;
        } else {
            String name = entry.getName();
            if (name.endsWith(".iwa")) {
                if (context.acceptIWAFile(name)) {
                    context.onBeginParseIWAFile(name);

                    try {
                        context.setCurrentFile(name);
                        if (this.type == "") {
                            this.type = this.getType(zis, name, context, target, zipIn);
                            if (this.type != "") {
                                this.parse(new File(this.path), target, this.path);
                            }
                        } else {
                            this.parseIWA(zis, name, context);
                        }
                    } catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
                        context.onEndParseIWAFile(name);
                    }
                } else {
                    context.onSkipFile(name, zis);
                }

                return true;
            } else {
                context.onSkipFile(name, zis);
                return false;
            }
        }
    }

    private void parseIWA(InputStream in, String filename, IwanaContext<T> context) throws IOException {
        MessageActions actions = context.getMessageTypeActions();
        InputStream bin = new SnappyNoCRCFramedInputStream(in, false);
        RestrictedSizeInputStream rsIn = new RestrictedSizeInputStream(bin, 0L);

        while(!Thread.interrupted()) {
            ArchiveInfo ai = ArchiveInfo.parseDelimitedFrom(bin);
            if (ai == null) {
                break;
            }

            Iterator messages = ai.getMessageInfosList().iterator();

            while(messages.hasNext()) {
                MessageInfo mi = (MessageInfo)messages.next();
                rsIn.setNumBytesReadable((long)mi.getLength());

                try {
                    actions.onMessage(rsIn, ai, mi, context);
                } catch (InvalidProtocolBufferException var14) {
                    this.handleInvalidProtocolBufferException(ai, mi, var14);
                } finally {
                    rsIn.skipRest();
                }
            }
        }

    }

    private String getType(InputStream in, String filename, IwanaContext<T> context, T target, InputStream zipIn) throws IOException {
        MessageActions actions = context.getMessageTypeActions();
        InputStream bin = new SnappyNoCRCFramedInputStream(in, false);
        RestrictedSizeInputStream rsIn = new RestrictedSizeInputStream(bin, 0L);

        while(!Thread.interrupted()) {
            ArchiveInfo ai = ArchiveInfo.parseDelimitedFrom(bin);
            if (ai == null) {
                break;
            }

            Iterator messages = ai.getMessageInfosList().iterator();

            while(messages.hasNext()) {
                MessageInfo mi = (MessageInfo)messages.next();
                rsIn.setNumBytesReadable((long)mi.getLength());
                if (this.keyActions.contains(mi.getType()) && !this.numActions.contains(mi.getType()) && !this.pageActions.contains(mi.getType())) {
                    return "key";
                }

                if (this.numActions.contains(mi.getType()) && !this.keyActions.contains(mi.getType()) && !this.pageActions.contains(mi.getType())) {
                    return "numbers";
                }

                if (this.pageActions.contains(mi.getType()) && !this.keyActions.contains(mi.getType()) && !this.numActions.contains(mi.getType())) {
                    return "pages";
                }
            }

            rsIn.skipRest();
        }

        return "";
    }

    protected void handleInvalidProtocolBufferException(ArchiveInfo ai, MessageInfo mi, InvalidProtocolBufferException e) throws InvalidProtocolBufferException {
        throw e;
    }

    protected abstract IwanaContext<T> newContext(String var1, T var2);
}
