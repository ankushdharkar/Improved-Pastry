//import scala.math._
import scala.util.Random
import scala.actors.Actor
import scala.actors.Actor._
import actors.TIMEOUT

case class msgDie()
case class msgKillAllSlaves()
case class msgStartSlaves(numofslvs: Int)
case class msgCreateSlaves(n: Int)
case class msgAddNeighbour(nptr: Array[scala.actors.OutputChannel[Any]])
case class msgRumour(s: Double, w: Double)
case class msgTellMe()
case class msgConfirm(a: Int)
case class msgFinish(nx:Double)
case class msgTopoligyBuilt()
case class msgAllSet()
case class msgSimpleRumour()
case class msgRemoveOneNeighbour()

class Slave(myId: Int, masterActor: Actor) extends Actor {

  val r = new Random()
  var nbr: Array[Slave] = null
  var s: Double = myId+1
  var w: Double = 1.0

  var rt1: Double = 23
  var rt2: Double = 1

  var nbrmap = scala.collection.mutable.HashMap.empty[Int, scala.actors.OutputChannel[Any]] //hashmap of neighbours
  var trckrumr = 0
  
  var busy=0

  def act() {
	  //	println("Slave-" + myId + "  = " + self)
    
    loop {
      if(busy==0)
      react {
        case msgAddNeighbour(nptrarr) => { 
          busy=1
          
         // println("Received Array of size "+nptrarr.size)
          
          for(k <- 0 until nptrarr.size) {
         	nbrmap += (nbrmap.size -> nptrarr(k))
         	//println(self + "  Added " + nptrarr(k) +" \t at ("+(nbrmap.size-1)+")")
          }
          
          sender ! msgAllSet()
          busy=0  
        }

        case msgTellMe() => {
          for (k <- 0 until nbrmap.size) {
            println("\tSlave-" + myId + "   >>> " + nbrmap(k))
          }
        }
         
        case msgSimpleRumour() => {
          busy=1
          //println("\t"+self+" got a rumour !")
      	 //println(self+"  :\tSending confirmation to >  "+sender)
          sender ! msgConfirm(1)
          trckrumr = trckrumr + 1
         
          if (trckrumr <10) {

            //println("Slave-" + myId + "  : " + (s / w))
           var cnfrm:Int =0
           
           do{
         	  var x = r.nextInt(nbrmap.size)    	  
         	  nbrmap(x) ! msgSimpleRumour()
         	  	  
         	  //println("\t"+self+" < Awaiting Confirmation from  "+ nbrmap(x) )
         	  receiveWithin(250) {
         	  		case msgConfirm(n) =>{
         	  		  cnfrm=n
         	  		 //println(self+"\tConfirmed ! < "+ nbrmap(x) )
         	  		 }
         	  		
         	  		  case TIMEOUT => {
         	  		    nbrmap(x) ! msgDie()
         	  		    //println(self+"   WATCHOUT ! : Timeout waiting for "+ nbrmap(x))
         	  		    }
         	  	}   
         	  
            }while(cnfrm!=1)
           } 
          
          else {
            //println("Slave-" + myId + "  : I Heard  it 10 times ")
            masterActor ! msgFinish(0) 
          }        
          busy=0       
        }

        case msgRumour(ss, ww) => {
          busy=1
          //println("\t"+self+" got a rumour !")
      	 //println(self+"  :\tSending confirmation to >  "+sender)
          sender ! msgConfirm(1)
          trckrumr = trckrumr + 1
          s = s + ss
          w = w + ww

          var d1: Double = (s / w) - rt1
          var d2: Double = (s / w) - rt2

          if (d1 < 0) { d1 = d1 * -1 }
          if (d2 < 0) { d2 = d2 * -1 }

          if (d1 > 0.0000000001 || d2 > 0.0000000001) {

            //println("Slave-" + myId + "  : " + (s / w))
           var cnfrm:Int =0
           
           do{
         	  var x = r.nextInt(nbrmap.size)    	  
         	  nbrmap(x) ! msgRumour(s/2, w/2)
         	  	  
         	  //println("\t"+self+" < Awaiting Confirmation from  "+ nbrmap(x) )
         	  receiveWithin(250) {
         	  		case msgConfirm(n) =>{
         	  		  cnfrm=n
         	  		 //println(self+"\tConfirmed ! < "+ nbrmap(x) )
         	  		 }
         	  		
         	  		  case TIMEOUT => {
         	  		    nbrmap(x) ! msgDie()
         	  		    //println(self+"   WATCHOUT ! : Timeout waiting for "+ nbrmap(x)) 
         	  		    //continue
         	  		  }
         	  	}   
         	  
            }while(cnfrm!=1)
              
  			  s = s / 2
  			  w = w / 2

  			  rt2 = rt1
  			  rt1 = s / w
          } 
          
          else {
            //println("Slave-" + myId + "  : I Heard  : " + (s / w))
            masterActor ! msgFinish(s/w) 
          }        
          busy=0
        }

        
        case msgRemoveOneNeighbour() =>{
          
          val rq = new Random()
          var x = rq.nextInt(nbrmap.size)
        
          val lst = nbrmap.size - 1
          nbrmap += (x -> nbrmap(lst))
          nbrmap -= (lst) 
        
        }
        
        case msgDie() => { exit() }
      }
    }
  }
}

object master {
  def main(args: Array[String]) {

    var t1 =0.0
    var t2 =0.0
    
    val n = args(0).toInt
    var slv: Array[Slave] = null

    slv = new Array[Slave](n)

    for (i <- 0 until n) {
      slv(i) = new Slave(i, self)
      slv(i).start
    }

    var b: Int = Math.sqrt(n).toInt

    
         
    var arr: Array[scala.actors.OutputChannel[Any]]=null
    
    //for all nodes
    for (i <- 0 until n) {

      var x1 = 0
      var x2 = 0;
      var x3 = 0;
      var x4 = 0;
      var x5 = 0;

      if (args(1).equals("fullnetwork")) {
   
        arr = new Array(n-1)
          var c:Int =0
          for (j <- 0 until n) {
            if(i!=j){
            	arr(c)=slv(j)
            	c=c+1
            }
          }
                    
          slv(i) ! msgAddNeighbour(arr)
      }
      
      else if (args(1).equals("line")) {
       var c:Int =0
        x1 = i + 1
        if (x1 < n) { c=c+1 }
        x2 = i - 1
        if (x2 >= 0) {c=c+1 }
      
        arr = new Array(c)
        c=0
        
        if (x1 < n) { 
      	  arr(c)=slv(x1)
           c=c+1
        }
        if (x2 >= 0) { 
      	arr(c)=slv(x2)
      	c=c+1
        }
        
        slv(i) ! msgAddNeighbour(arr)
        
      } 
      
     else if (args(1).equals("2Dgrid")) {
      var c:Int =0
      
        x1 = i + 1
        if ((x1 / b) == (i / b) && x1 < n) {c=c+1 }

        x2 = i + b
        if (x2 < n) { c=c+1 }

        x3 = i - 1
        if ((x3 / b) == (i / b) && x3 >= 0) {c=c+1 }

        x4 = i - b
        if (x4 >= 0) {c=c+1 }
        
        arr = new Array(c)
        c=0
        
         x1 = i + 1
        if ((x1 / b) == (i / b) && x1 < n) {       	  
        	arr(c)=slv(x1)
         c=c+1 
         }

        x2 = i + b
        if (x2 < n) {
        	arr(c)=slv(x2)
         c=c+1 
         }

        x3 = i - 1
        if ((x3 / b) == (i / b) && x3 >= 0) {
        	arr(c)=slv(x3)
         c=c+1
         }

        x4 = i - b
        if (x4 >= 0) {
        	arr(c)=slv(x4)
         c=c+1 
         }
        
        slv(i) ! msgAddNeighbour(arr)
      } 
      
     else if (args(1).equals("imperfect2Dgrid")) {
         var c:Int =0
      
        	x1 = i + 1
        	if ((x1 / b) == (i / b) && x1 < n) {c=c+1 }

        x2 = i + b
        if (x2 < n) { c=c+1 }

        x3 = i - 1
        if ((x3 / b) == (i / b) && x3 >= 0) {c=c+1 }

        x4 = i - b
        if (x4 >= 0) {c=c+1 }
        
        arr = new Array(c+1)
        c=0
        
         x1 = i + 1
        if ((x1 / b) == (i / b) && x1 < n) {       	  
        	arr(c)=slv(x1)
         c=c+1 
         }

        x2 = i + b
        if (x2 < n) {
        	arr(c)=slv(x2)
         c=c+1 
         }

        x3 = i - 1
        if ((x3 / b) == (i / b) && x3 >= 0) {
        	arr(c)=slv(x3)
         c=c+1
         }

        x4 = i - b
        if (x4 >= 0) {
        	arr(c)=slv(x4)
         c=c+1 
         }
        
      
        val rg = new Random()
        do{
      	  x5 = rg.nextInt(n)
        }while(x5==x1 || x5==x2 || x5==x3 || x5==x4 || x5==i);
      	arr(c)=slv(x5)
      	
      slv(i) ! msgAddNeighbour(arr)      
     }

//      receive{
//        case msgTopoligyBuilt() =>{}
//      }
     
    } //setting topology loop
    
    for (i <- 0 until n) {
      receive{
        case msgAllSet() =>{}
      }
    }
    
    println("Topology is now set !")

    //to print neighbours
   /*for (i <- 0 until n) {
      println("Print Neighbours for :  "+slv(i))
      slv(i) ! msgTellMe()
      Thread.sleep(1000)
    }*/

    
    val r = new Random()
    var x = 0


      var a: Int = 0
     
      t1=System.currentTimeMillis()
       do{
      	x = r.nextInt(n)
      	//println(self + "\tSending to "+slv(x))
      	
      	if(args(2).equals("gossip")){
      	  slv(x)! msgSimpleRumour()
      	}
      	
      	else if(args(2).equals("push-sum")){  
      		slv(x)! msgRumour(0,0)
      	}
      	receive {
      		case msgConfirm(n) =>{a=n}
      		case TIMEOUT => {println("<RUNNER> WATCHOUT ! : Timeout :: "+slv(x))}
      	} 	
      }while(a!=1)
      
    

    println("Failure Begins...")
    if(args.size ==5){
      
      if(args(3).equals("connectionfailure")){
        
         val rq = new Random()
         var x =0
         
         for (i <- 0 until args(4).toInt) {
         	x = rq.nextInt(n)
           slv(x)! msgRemoveOneNeighbour()
      	}  
      }
      else  if(args(3).equals("nodefailure")){
        
         val rq = new Random()
         var x =0
         
         for (i <- 0 until args(4).toInt) {
         	x = rq.nextInt(n)
      	   slv(x)! msgDie()
         }  
      }
      
    }
    
    
    receive{
      case msgFinish(nx) =>{
        println(nx)
        t2=System.currentTimeMillis()-t1
        println("Time Taken = "+t2+" ms")
        }
      
      
      for (i <- 0 until n) {
      	slv(i) ! msgDie()
      } 
    }//receive final 
  }//main
}//onject