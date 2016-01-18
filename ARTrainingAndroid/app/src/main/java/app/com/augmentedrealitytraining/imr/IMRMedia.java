package app.com.augmentedrealitytraining.imr;


public class IMRMedia {
    private int id;
    private String mediaType;
    private String title;
    private String description;
    private String url;
    private String path;
    private String sequence;
    private String place;
    private boolean read;

    public IMRMedia(int a,String type,String t,String d,String u,String p,String s,String pl)
    {
        id=a;
        mediaType=type;
        title=t;
        description=d;
        url=u;
        path=p;
        sequence=s;
        place=pl;

    }

    public int getId()
    {
        return id;
    }

    public String getMediaType()
    {
        return mediaType;
    }
    public String getTitle()
    {
        return title;
    }
    public String getDescription()
    {
        return description;
    }
    public String getUrl()
    {
        return url;
    }
    public String getPath()
    {
        return path;
    }
    public String getSequence()
    {
        return sequence;
    }
    public String getPlace()
    {
        return place;
    }

    @Override
    public String toString()
    {
        return title;
    }

    public void setRead(boolean value)
    {
        read=value;
    }
    public boolean isRead()
    {
        return read;
    }

}
