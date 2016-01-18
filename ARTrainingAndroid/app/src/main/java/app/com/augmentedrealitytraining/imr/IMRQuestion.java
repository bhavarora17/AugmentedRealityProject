package app.com.augmentedrealitytraining.imr;


public class IMRQuestion {
    private int questionId;
    private String hints;
    private String questionType;
    private String title;
    private String[] questions;
    private String answer;

    public IMRQuestion(int id,String h,String type,String t,String[] mquestion,String a)
    {
        questionId=id;
        hints=h;
        questionType=type;
        title=t;
        answer=a;
        questions=new String[mquestion.length];
        for(int i=0;i<mquestion.length;i++)
        {
            questions[i]=mquestion[i];
        }
    }

    public int getQuestionId()
    {
        return questionId;
    }
    public String getHints()
    {
        return hints;
    }
    public String getQuestionType()
    {
        return questionType;
    }
    public String getTitle()
    {
        return title;
    }
    public String[] getQuestion()
    {
        return questions;
    }
    public String getAnswer()
    {
        return answer;
    }
}
