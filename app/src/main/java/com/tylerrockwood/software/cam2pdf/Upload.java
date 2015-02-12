package com.tylerrockwood.software.cam2pdf;

/**
 * Created by rockwotj on 2/10/2015.
 */
public class Upload {

    private long mId;
    private String mName;
    private String mPath;
    private String mSize;
    private String mParentFolder;
    private String mCreationDate;

    public Upload(long id, String name, String path, String size, String parentFolder, String creationDate) {
        this.mId = id;
        this.mName = name;
        this.mPath = path;
        this.mSize = size;
        this.mParentFolder = parentFolder;
        this.mCreationDate = creationDate;
    }

    public String getCreationDate() {
        return mCreationDate;
    }

    public String getPath() {
        return mPath;
    }

    public String getSize() {
        return mSize;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return "https://drive.google.com/open?mId=" + mParentFolder + "&authuser=0";
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }
}
