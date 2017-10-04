package de.daug.semanticchess.Annotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.util.CoreMap;
//https://nlp.stanford.edu/software/sutime.html
public class TimeTagger {

  private String date;
  private DateFormat dateFormat;
  private AnnotationPipeline pipeline = null;
  
  public TimeTagger(){
	  Properties props = new Properties();
	    pipeline = new AnnotationPipeline();
	    pipeline.addAnnotator(new TokenizerAnnotator(false));
	    pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
	    pipeline.addAnnotator(new POSTaggerAnnotator(false));
	    pipeline.addAnnotator(new TimeAnnotator("sutime", props));

	    this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    this.date = dateFormat.format(new Date());
  }
  
  public String getDate(String dateStr){
	  
	  Annotation annotation = new Annotation(dateStr);
      annotation.set(CoreAnnotations.DocDateAnnotation.class, date);
      pipeline.annotate(annotation);
      List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
      for (CoreMap cm : timexAnnsAll) {
        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        dateStr = cm.get(TimeExpression.Annotation.class).getTemporal().toString().replace("-", ".").replaceAll(".XX", "");
      }
	  return dateStr;
  }
 
  /** Example usage:
   *  java SUTimeDemo "Three interesting dates are 18 Feb 1997, the 20th of july and 4 days from today."
   *
   *  @param args Strings to interpret
   */
  public static void main(String[] args) {
    TimeTagger tt = new TimeTagger();
    System.out.println(tt.getDate("from the 18th century"));
    
  }

}