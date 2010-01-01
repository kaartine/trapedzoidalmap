/****************************************************
 * This class handels the InfoBox that reports to the
 * user the state of the algorithm.
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

import java.awt.*;
import java.awt.TextComponent;

class InfoBox extends Panel {

	TextArea field = null;
	String newline = "\n";
   
   InfoBox() {
   
   	//Create the text field and make it uneditable.
 		field = new TextArea(5, 81);
      field.setEditable(false);
 
 		//Add the text field to the applet.
      add(field);
      validate();
 	}

	public void addItem(String newWord) {
   	//This used to append the string to the StringBuffer;
      //now it appends it to the TextArea.
      field.append(newline + newWord);
  	}
       
  /**
   * Puts a little breathing space between
   * the panel and its contents, which lets us draw a box
   * in the paint() method.
   */
   public Insets getInsets() {
   	return new Insets(2,2,2,2);
	}   
}