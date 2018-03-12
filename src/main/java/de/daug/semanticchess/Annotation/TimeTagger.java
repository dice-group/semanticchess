package de.daug.semanticchess.Annotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.util.CoreMap;


/**
 * This class uses https://nlp.stanford.edu/software/sutime.html
 * It analyzes dates in the query and returns date string (yyyy-mm-dd).
 */
public class TimeTagger {

  private String date;
  private DateFormat dateFormat;
  private StanfordCoreNLP pipeline = null;
  
  /**
   * constructor: sets up pipelines
   */
  public TimeTagger(){
	  	Properties props = new Properties();
		// props.setProperty("ner.applyNumericClassifiers", "true");
		props.setProperty("annotators", "tokenize, ssplit");	  	
	  	
	    pipeline = new StanfordCoreNLP(props);
	    pipeline.addAnnotator(new TimeAnnotator("sutime", props));

	    this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    this.date = dateFormat.format(new Date());
  }
  
  /**
   * formats the dates 
   * @param dateStr: String that should be configured
   * @return configured dateStr
   */
  public String getDate(String dateStr){
	  
	  Annotation annotation = new Annotation(dateStr);
      annotation.set(CoreAnnotations.DocDateAnnotation.class, date);
      pipeline.annotate(annotation);
      List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
      for (CoreMap cm : timexAnnsAll) {
        //List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
        dateStr = cm.get(TimeExpression.Annotation.class).getTemporal().toString().replace("-", ".").replaceAll(".XX", "");
      }
	  return dateStr;
  }
 
  /** 
   * main method for testing
   */
  public static void main(String[] args) {
    TimeTagger tt = new TimeTagger();
    System.out.println(tt.getDate("games from June 28th 2017"));
    
  }

}