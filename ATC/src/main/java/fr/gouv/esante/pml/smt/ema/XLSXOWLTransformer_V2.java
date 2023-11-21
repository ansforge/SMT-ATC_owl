package fr.gouv.esante.pml.smt.ema;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConstants;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import fr.gouv.esante.pml.smt.utils.ADMSVocabulary;
import fr.gouv.esante.pml.smt.utils.AnsVocabulary;
import fr.gouv.esante.pml.smt.utils.ChargerMapping;
import fr.gouv.esante.pml.smt.utils.DCTVocabulary;
import fr.gouv.esante.pml.smt.utils.DCVocabulary;
import fr.gouv.esante.pml.smt.utils.PropertiesUtil;
import fr.gouv.esante.pml.smt.utils.SKOSVocabulary;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
import org.semanticweb.owlapi.vocab.OWL2Datatype.Category;




public class XLSXOWLTransformer_V2 {
	
	//*********
	private static String xlsxATCFileName = PropertiesUtil.getProperties("xlsATCFile");
	  private static String owlATCFileName = PropertiesUtil.getProperties("owlATCFileName");
	  static final String TSTAMP =
		        "-?([1-9][0-9]{3,}|0[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])T(([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\\.[0-9]+)?|(24:00:00(\\.0+)?))(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))";
		  

	  private static OWLOntologyManager man = null;
	  private static OWLOntology onto = null;
	  private static OWLDataFactory fact = null;
	  
	  private static OWLAnnotationProperty skosNotation  = null;
	  private static OWLAnnotationProperty rdfsLabel  = null;
	  private static OWLAnnotationProperty dcType  = null;
	  private static OWLAnnotationProperty dctCreated  = null;
	  private static OWLAnnotationProperty dctModified  = null;
	  private static OWLAnnotationProperty admsStatus  = null;
	  private static OWLAnnotationProperty rdfsComments  = null;
	  private static OWLAnnotationProperty dctReplacedBy  = null;
	  private static OWLAnnotationProperty owlDeprecated  = null;
	  
	  
	
	public static void main(String[] args) throws Exception {
		
		ChargerMapping.chargeExcelConceptToList(xlsxATCFileName);
		  
		final OutputStream fileoutputstream = new FileOutputStream(owlATCFileName);
		 man = OWLManager.createOWLOntologyManager();
		 onto = man.createOntology(IRI.create(PropertiesUtil.getProperties("terminologie_IRI")));
		 fact = onto.getOWLOntologyManager().getOWLDataFactory();
		
		 skosNotation =  fact.getOWLAnnotationProperty(SKOSVocabulary.NOTATION.getIRI());
		 rdfsLabel =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_LABEL.getIRI());
		 dcType = fact.getOWLAnnotationProperty(DCVocabulary.type.getIRI());
		 dctCreated = fact.getOWLAnnotationProperty(DCTVocabulary.created.getIRI());
		 dctModified = fact.getOWLAnnotationProperty(DCTVocabulary.modified.getIRI());
		 admsStatus = fact.getOWLAnnotationProperty(ADMSVocabulary.status.getIRI());
		 
		 rdfsComments =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		 
		 dctReplacedBy =  fact.getOWLAnnotationProperty(DCTVocabulary.isReplacedBy.getIRI());
		 owlDeprecated =  fact.getOWLAnnotationProperty(fact.getOWLDeprecated());
		 
		 
		    OWLClass owlClass = null;
		    
		    
		    createPrincipalNoeud();
		    createConceptRetiresNoeud();
		    createConceptRetires2023Noeud();
		    

		    
		    for(String id: ChargerMapping.listConceptsATC.keySet()) {
		    	final String about = PropertiesUtil.getProperties("terminologie_URI") + id;
		        owlClass = fact.getOWLClass(IRI.create(about));
		        OWLAxiom declare = fact.getOWLDeclarationAxiom(owlClass);
		        man.applyChange(new AddAxiom(onto, declare));
		        
		        
		        if("active".equals(ChargerMapping.listConceptsATC.get(id).get(5))) {
		        	
		        String aboutSubClass = null;
		        aboutSubClass = PropertiesUtil.getProperties("terminologie_URI")+ ChargerMapping.listConceptsATC.get(id).get(1)  ;
		        OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
		        
		        OWLAxiom axiom = fact.getOWLSubClassOfAxiom(owlClass, subClass);
		        man.applyChange(new AddAxiom(onto, axiom));
		       
		        }else {
		        	
		        	
		        	String aboutSubClass = null;
			        aboutSubClass = PropertiesUtil.getProperties("terminologie_URI")+ "Concept_retirés_2023"  ;
			        OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
			        
			        OWLAxiom axiom = fact.getOWLSubClassOfAxiom(owlClass, subClass);
			        man.applyChange(new AddAxiom(onto, axiom));
		        	
		        	
		        }
		        
		        
		       
                
		        
		        
		        addLateralAxioms(skosNotation, id, owlClass);
		        addLateralAxioms(dcType, ChargerMapping.listConceptsATC.get(id).get(0), owlClass);
		        addLateralAxioms(rdfsLabel, ChargerMapping.listConceptsATC.get(id).get(2), owlClass, "en");
		        addLateralAxioms(rdfsLabel, ChargerMapping.listConceptsATC.get(id).get(3), owlClass, "fr");
		        addDatelAxioms(dctCreated, ChargerMapping.listConceptsATC.get(id).get(4), owlClass);
		        addLateralAxioms(admsStatus, ChargerMapping.listConceptsATC.get(id).get(5), owlClass);
		       
		        if(ChargerMapping.listConceptsATC.get(id).get(6)!= "") {
		        
		          addURIAxioms(dctReplacedBy, ChargerMapping.listConceptsATC.get(id).get(6), owlClass);
		          addBooleanAxioms(owlDeprecated, "true", owlClass);
		          
		        }
		       
		        if(ChargerMapping.listConceptsATC.get(id).get(7)!= "")
		        	addDatelAxioms(dctModified, ChargerMapping.listConceptsATC.get(id).get(7), owlClass);
			     
		        if(ChargerMapping.listConceptsATC.get(id).get(8)!= "")
		        	addLateralAxioms(rdfsComments, ChargerMapping.listConceptsATC.get(id).get(8), owlClass);
			      
		      
		       
		     
		      
		       
		       
		      
		        
		    }
		    
		    
		    final RDFXMLDocumentFormat ontologyFormat = new RDFXMLDocumentFormat();
		    ontologyFormat.setPrefix("dct", "http://purl.org/dc/terms/");
		   
		    
		    
		    IRI iri = IRI.create(PropertiesUtil.getProperties("terminologie_IRI"));
		    man.applyChange(new SetOntologyID(onto,  new OWLOntologyID(iri)));
		   
		  //  addPropertiesOntology();
		    
		    man.saveOntology(onto, ontologyFormat, fileoutputstream);
		    fileoutputstream.close();
		    System.out.println("Done.");
		
		

	}
	
	public static void addLateralAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addLateralAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass, String lang) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val, lang));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addDatelAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	  
	   // final OWLAnnotation annotation =
	    //		fact.getOWLAnnotation(prop, fact.getOWLLiteral(val, OWL2Datatype.XSD_DATE_TIME));

	   final OWLAnnotation annotation =
	    		fact.getOWLAnnotation(prop, fact.getOWLLiteral(val, OWL2Datatype.XSD_DATE_TIME));

	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  
  public static void addURIAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {

	    IRI iri_creator = IRI.create(PropertiesUtil.getProperties("terminologie_URI")+val);
		   
	    OWLAnnotationProperty prop_creator =fact.getOWLAnnotationProperty(prop.getIRI());
	    
	    OWLAnnotation annotation = fact.getOWLAnnotation(prop_creator, iri_creator);
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	    
	    
	  }
  
  
  public static void addBooleanAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val,OWL2Datatype.XSD_BOOLEAN));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  
  
  public static void createPrincipalNoeud() {
	  
	   String parent_label_en=PropertiesUtil.getProperties("label_noeud_parent_en");
	   String parent_label_fr=PropertiesUtil.getProperties("label_noeud_parent_fr");

	   String noeud_parent_notation=PropertiesUtil.getProperties("notation_noeud_parent");
	    
	   final String classRacine = PropertiesUtil.getProperties("URI_parent") ;
	   OWLClass noeudRacine = fact.getOWLClass(IRI.create(classRacine));
       addLateralAxioms(skosNotation, noeud_parent_notation, noeudRacine);
       addLateralAxioms(rdfsLabel, parent_label_fr, noeudRacine, "fr");
       addLateralAxioms(rdfsLabel, parent_label_en, noeudRacine, "en");
	  
  }
  
  
  public static void createConceptRetires2023Noeud() {
	  
	  
	    
	   final String classRacine = "http://data.esante.gouv.fr/whocc/atc/Concept_retirés_2023" ;
	   OWLClass noeudRacine = fact.getOWLClass(IRI.create(classRacine));
	   
	   String aboutSubClass = null;
       aboutSubClass = "http://data.esante.gouv.fr/whocc/atc/Concept_retirés"  ;
       OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
       
       OWLAxiom axiom = fact.getOWLSubClassOfAxiom(noeudRacine, subClass);
       man.applyChange(new AddAxiom(onto, axiom));
     
	  addLateralAxioms(skosNotation, "Concept retirés 2023", noeudRacine);
      
	  
 }
  
  
   public static void createConceptRetiresNoeud() {
	  
	  
	    
	   final String classRacine = "http://data.esante.gouv.fr/whocc/atc/Concept_retirés" ;
	   OWLClass noeudRacine = fact.getOWLClass(IRI.create(classRacine));
	    addLateralAxioms(skosNotation, "Concept retirés", noeudRacine);
      
	  
 }
  
  
   
  
  
  
  

}
