package app.com.augmentedrealitytraining.imr;

import android.content.Context;

import java.util.ArrayList;

//singletons
public class IMRData {

    private static IMRData sIMRData;
    private ArrayList<IMRMedia> mediaList;
    private ArrayList<IMRQuestion> questionList;
    private Context mAppContext;
    private ArrayList<String> completeList;

    private IMRData(Context appContext)
    {
        mAppContext=appContext;
        mediaList=new ArrayList<IMRMedia>();
        questionList=new ArrayList<IMRQuestion>();
        completeList=new ArrayList<String>();
    }

    public static IMRData get(Context c)
    {
        if(sIMRData==null)
        {
            sIMRData=new IMRData(c.getApplicationContext());
        }
        return sIMRData;
    }

    public ArrayList<IMRMedia> getMediaList()
    {
        return mediaList;
    }
    public IMRMedia getMedia(int id)
    {
        for(IMRMedia m:mediaList)
        {
            if(m.getId()==id)
            {
                return m;
            }
        }
        return null;
    }

    public void updateMediaList(String title)
    {
        for(int i=0;i<mediaList.size();i++)
        {
            IMRMedia m=mediaList.get(i);
            if(m.getTitle().equals(title))
            {
                m.setRead(true);
                mediaList.set(i,m);
            }
        }
    }
    public boolean isMediaListEmpty()
    {
        if(mediaList.isEmpty())
        {
            return true;
        }
        else
            return false;
    }
    public void addMedia(IMRMedia a)
    {
        mediaList.add(a);
    }

    public ArrayList<IMRQuestion> getQuestionList()
    {
        return questionList;
    }
    public IMRQuestion getIMRQuestion(int id)
    {
        for(IMRQuestion q:questionList)
        {
            if(q.getQuestionId()==id)
            {
                return q;
            }
        }
        return null;
    }
    public void addQustion(IMRQuestion Q)
    {
        questionList.add(Q);
    }

    public boolean isQuestionListEmpty()
    {
        if(questionList.isEmpty())
        {
            return  true;
        }
        else
            return false;
    }

    public ArrayList<String> getCompleteList()
    {
        return completeList;
    }

    public void addComplete(String title)
    {
        completeList.add(title);
    }

}
