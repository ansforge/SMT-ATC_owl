package fr.gouv.esante.pml.smt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ChargerMapping {
	
	public static HashMap<String, List<String>> listConceptsATC = new HashMap<String, List<String>>();

	public static  void  chargeExcelConceptToList(final String xlsFile) throws IOException, ParseException, InvalidFormatException {
		
	//public static void main(String[] args)  throws IOException, ParseException, InvalidFormatException {
		
		
		
      //String xlsFile = PropertiesUtil.getProperties("xlsATCFile");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		FileInputStream file = new FileInputStream(new File(xlsFile));
		
		//XSSFWorkbook workbook = new XSSFWorkbook(file);
		org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file);


		//XSSFSheet sheet = workbook.getSheetAt(0);
		
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

		
		Iterator<Row> rowIterator = sheet.iterator();
		
		rowIterator.next(); 
		
		
		while (rowIterator.hasNext()) {
			 
			 
			 Row row = rowIterator.next();
	    	 Cell niveauCode = row.getCell(0); 
		     Cell atcCode = row.getCell(1); 
		     Cell atcCodePere = row.getCell(2); 
		     Cell libelleAnglais = row.getCell(3); 
		     Cell libelleFrancais = row.getCell(4); 
		     Cell commantaires = row.getCell(5);
		     
		     Cell dateCreation = row.getCell(6);
		     Cell dateModified = row.getCell(7);
		     Cell dateInactivation = row.getCell(8);
		     
		     String codePere = atcCodePere.getStringCellValue();
		     if ("-".equals(atcCodePere.getStringCellValue())) {
		    	 codePere ="ATC";
		     }
		    
		     // System.out.println(" "+atcCode.getStringCellValue());
			  List<String> listedonnees= new ArrayList<>();
			  listedonnees.add(0,  String.valueOf((int)niveauCode.getNumericCellValue()));
			  listedonnees.add(1, codePere);
			  listedonnees.add(2, libelleAnglais.getStringCellValue());
			  
			  if(libelleFrancais!=null)
			   listedonnees.add(3, libelleFrancais.getStringCellValue());
			  else
			   listedonnees.add(3, "");
			  
			  //Date Creation
			  if(dateCreation!=null) {
			      
				  listedonnees.add(4, formatter.format(returnDate("01/01/2023")));
			  }
			  else {
			   listedonnees.add(4, formatter.format(returnDate("01/01/2022"))); 
			  }
			  
			
			  
			  
			  //Date Inactivation
			  if(dateInactivation!=null) {
				    listedonnees.add(5, "inactive");
				    listedonnees.add(6,  getCode(commantaires.getStringCellValue()));
				     
			  }	    
				  else {
				    listedonnees.add(5, "active");
				    listedonnees.add(6, "");
				    
				  }	
			  
			 System.out.println(atcCode.getStringCellValue()); 
			 
			//Date Modified
			  if(dateModified!=null && dateModified.getCellType()!= Cell.CELL_TYPE_BLANK) {
				  System.out.println(dateModified.getDateCellValue()); 
				  listedonnees.add(7, formatter.format(dateModified.getDateCellValue()));
			  }
			  else if(dateInactivation!=null) {
				  listedonnees.add(7, formatter.format(dateInactivation.getDateCellValue())); 
			  }else {
				  listedonnees.add(7, ""); 
			  }
			  
			  
			  
              if(commantaires!=null) {
				   listedonnees.add(8, commantaires.getStringCellValue());
			  }
			  else  {
			      listedonnees.add(8, ""); 
			  }
			  
			  

             listConceptsATC.put(atcCode.getStringCellValue(), listedonnees);
     
		   
		}
		
		 List<String> liste = listConceptsATC.get("A");
	     
	    


		//return listConceptsEma;
	    

	}
	
   
   
   
   private static Date returnDate(String dateInString) throws ParseException {
  	 
	   SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

       Date date = formatter.parse(dateInString);
           
	   return date;

       
    }
   
   private static String getCode(String commantaires) {
	   
	   String[] cmts = commantaires.split("par");
	   
	   return cmts[1].trim();
   
   }

}
