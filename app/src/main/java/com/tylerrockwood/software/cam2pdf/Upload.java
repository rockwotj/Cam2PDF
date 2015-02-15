package com.tylerrockwood.software.cam2pdf;

import java.util.List;

/**
 * Created by rockwotj on 2/10/2015.
 */
public class Upload implements Comparable<Upload> {

    private long mId;
    private String mName;
    private String mPath;
    private String mSize;

    public String getParentFolder() {
        return mParentFolder;
    }

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
        return "https://drive.google.com/open?id=" + mParentFolder + "&authuser=0";
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }

    @Override
    public int compareTo(Upload another) {
        String otherDate = another.getCreationDate();
        String others[] = otherDate.split("/");
        int otherMonth = Integer.parseInt(others[0]);
        int otherDay = Integer.parseInt(others[1]);
        int otherYear = Integer.parseInt(others[2]);

        String theseDates[] = this.mCreationDate.split("/");
        int thisMonth = Integer.parseInt(theseDates[0]);
        int thisDay = Integer.parseInt(theseDates[1]);
        int thisYear = Integer.parseInt(theseDates[2]);

        if (otherYear > thisYear) {
            return -1;
        } else if (otherYear == thisYear) {
            if (otherMonth > thisMonth) {
                return -1;
            } else if (otherMonth == thisMonth) {
                if (otherDay > thisDay)
                    return -1;
                else if (otherDay == thisDay)
                    return 0;
                else
                    return 1;
            } else
                return 1;
        } else
            return 1;
    }
}
