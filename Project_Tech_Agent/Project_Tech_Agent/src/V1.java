import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.awt.TextArea;
public class V1 extends Agent {

 //
	static int x=1;
   Produit E=new Produit();
   ACLMessage m;
   static long start; long time;
   
   static double max=0;
    static int nbrAcheteur ,n;
    
    Object []o=new Object[2];
    int cont;
    AID LastAidMax;
    TextArea text;
     
  //les getters et les setters  
   public int getNbrAcheteur() {
	return nbrAcheteur;
}
  public  void setNbrAcheteur(int nbrAcheteur) {
	this.nbrAcheteur = nbrAcheteur;
}
  
  //liste des AID des acheteurs
  ArrayList<AID> BuyersAgents=new ArrayList<AID>(); 
  ArrayList<AID> ListAgentQuitter=new ArrayList<AID>(); 
  ArrayList<AID> ListAgentQuitterCopie=new ArrayList<AID>(); 
   AID agentID;
   //liste des acheteurs 
   ArrayList<Acheteur>ListeAcheteurs;
  
   public V1() {  
   }
   
   protected void setup() { 
    
	   Object[]args=getArguments();
	   if(args!=null) {
		   E.setPrix(Double.valueOf((String)(args[0])).doubleValue());
		 
		   E.setPrixReserve(Double.valueOf((String)(args[1])).doubleValue());
		   
		   
		    time=(long) args[5];
		    text=(TextArea)args[3];
		   nbrAcheteur=(int)args[2];
		    E.setDesignation((String) args[4]);
		   o[0]=E;
		   o[1]=0;
		   
		// envoi du prix init aux acheteurs
		   m=new ACLMessage(ACLMessage.INFORM);
		
		   for(int i=1; i<=nbrAcheteur; i++) {
			  BuyersAgents.add(new AID("Acheteur"+(i),AID.ISLOCALNAME));
			 
			  m.addReceiver(BuyersAgents.get(i-1));
			  
			  System.out.println("agent ="+BuyersAgents.get(i-1).getLocalName());
			  
	         }
		   try {
				 m.setContentObject(o);
				 m.setLanguage("JavaSerialization");
				   //  System.out.println("Message=="+m.getContent());
				      send(m);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   
	   }
	   
	   start=System.currentTimeMillis();
	    
	   addBehaviour(new CyclicBehaviour(this){
		   public void action() {
			   ACLMessage m2 = null;
			  	
				
		//recevoir l'object E
		//start=System.currentTimeMillis();
			   System.out.println("strat="+(long)start);
			   
		
			   n=BuyersAgents.size();

	// text.appendText("\n------------------------les prix reçus par le vendeur-----------------------------\n");
		  
		 while(n!=0){
		   m2=blockingReceive(); 
		    n--;
		    while(m2==null) {
		    	try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		   try {
			   //recuperer le produit de la part de l'acheteur
			  o=(Object[]) m2.getContentObject();
			  
			  E=(Produit)o[0];
			 // System.out.println("le prix nouveau de la part de l'acheteur x ="+E.getPrix());
			  cont=(int)o[1];
			  System.out.println(cont);
			  if(cont==0) {
			 
			  if(E.getPrix()>max) {max=E.getPrix(); LastAidMax=m2.getSender();
			 
			 // text.appendText("\n\t\tje suis "+getLocalName()+" j'ai reçu le prix "+E.getPrix()+" de la part de:  "+m2.getSender().getLocalName());
				
			  }
			 
			  }
			  else  if(cont==-1){
				  //recuprer le AID de l'acheteur
				 // text.appendText("\n\t\tje suis "+getLocalName()+"j'ai reçu le prix"+E.getPrix()+"de la part de: "+m2.getSender().getLocalName());
					
				  agentID=m2.getSender();
				  ListAgentQuitter.add(agentID);
			   //supprimer l'agennt de la liste des agnents
				//  System.out.println("l'agent "+agentID.getLocalName()+"qui veut quitter");
			  }
			  
		   }
	      catch(UnreadableException e) {
				    System.err.println(getLocalName()+"catched exception"+e.getMessage());
		  }  
		 
		   }block();
		  // text.appendText("\n max = "+max);
		 text.appendText("\n--------Je suis "+getLocalName()+" j'annonce le plus grand prix reçu ="+max+"---------");
		   System.out.println("max final= "+max);
		     //tester si il y'a des agents qui veulent quitter pour lui dire ok 
		     
		     
		     //reply.setPerformative(ACLMessage.INFORM);$
		     int i=0;
		     ACLMessage mAgentDelet=new ACLMessage(ACLMessage.INFORM);
				
		     while((ListAgentQuitter.isEmpty()==false) && (i<ListAgentQuitter.size()) ){
		    	 o[1]=1;
		    	 o[0]=null;
		    	 mAgentDelet.addReceiver(ListAgentQuitter.get(i));
		    	 try {
		    		 mAgentDelet.setContentObject(o);
		    		 mAgentDelet.setLanguage("JavaSerialization");
					send(mAgentDelet);
					
				     
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    	int j=0;
		    	while (!BuyersAgents.isEmpty() && j<BuyersAgents.size()) {
		    		AID A=BuyersAgents.get(j);
		    		if((!ListAgentQuitter.isEmpty())&&(!BuyersAgents.isEmpty())&& A.equals(ListAgentQuitter.get((i))) )
		    		{
		    			 m.removeReceiver(ListAgentQuitter.get(i));
				    	 ListAgentQuitterCopie.add(ListAgentQuitter.get(i));
				    	 ListAgentQuitter.remove(i);
				    	BuyersAgents.remove(A);
				    
		    		}
		    		j++;
		    		 
		    	}
		    	 
		    	//
		    	 i++;
		     }
		     //si y'a un au moins parmi les acheteurs en cours
		     long duree=System.currentTimeMillis()-start;
		     System.out.println("duree="+duree);
		     if((!BuyersAgents.isEmpty()) &&(duree<time)) {
		   
              E.setPrix(max);   
              o[0]=E;
              o[1]=0;
       	    try {
       	   // reply.setContent("ok");
       		m.setContentObject(o);
       		m.setLanguage("JavaSerialization");
       		send(m);
       	} catch (IOException e1) {
       		// TODO Auto-generated catch block
       		e1.printStackTrace();
       	}  
		     }
		
		     //si tous les achteurs s'arretent
		     else { 
		    	 
		    	 //arreter l'ecnchere 
		    	 //trouver le gangnat 
		    	 //si le dernier prix reçu par le vendeur est plus haut qu'un prix de reserve le produit sera vendu 
		    	  int j=0;
		    	  AID A = null;

		    	   if(duree>=time) {
		    		   //supprimer les achteurs 
		    		   System.out.println("on a dépassé le temps de l'enchère");
		    		   text.appendText("\n----------------Le temps de l'enchère  est terminé.-------------------");
		    		   o[0]=null;
		               o[1]=2;
		               try {
		               	   // reply.setContent("ok");
		               		m.setContentObject(o);
		               		m.setLanguage("JavaSerialization");
		               		send(m);
		               	} catch (IOException e1) {
		               		// TODO Auto-generated catch block
		               		e1.printStackTrace();
		               	}  
		               
		    	   }
		    	   
		    	   
		    	   
		    	   
		    	   if(max>=E.getPrixReserve()) {
		    		   text.appendText("\n---------------Le gagnant est : "+LastAidMax.getLocalName()+" avec un prix de "+max+"-----------");
			    	   System.out.println("le gangnat est "+LastAidMax.getLocalName()+"avec un prix "+max);}
	    		   else {
	    			   text.appendText("\n-----------------Pas de gagnant pour cette enchère!---------------");
	    			     System.out.println("il n'existe pas un gangnat!");}
		    	 
		    	    doDelete();
		    	    
		    	  // Runtime.getRuntime().exit(V1.AP_DELETED);
		    	 
		     }	   
	 
		   }
	    
	   });
	  
   }
}