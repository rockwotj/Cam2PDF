package com.tylerrockwood.software.cam2pdf;

/**
 * Created by rockwotj on 2/10/2015.
 */
public class Upload {

    private long mId;
    private String mName;
    private String mParentFolder;
    private String mCreationDate;

    public Upload(long id, String name, String parentFolder, String creationDate) {
        this.mId = id;
        this.mName = name;
        this.mParentFolder = parentFolder;
        this.mCreationDate = creationDate;
    }

    public String getCreationDate() {
        return mCreationDate;
    }

    public String getParentFolder() {
        return mParentFolder;
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
