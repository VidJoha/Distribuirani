Prije pokretanja pronađite hostname pomoću hostname -I i to zalijepite u Symbols.java pod nameServer. 
(Ovako ja radim, ne znam jel imate bolji nacin) Nakon toga trebate recompilirat clase.
odite u com/mycompany/projektdist i izvrsite javac *.java
Nakon toga se vratite u src/main/java i tamo pokrenite java com.mycompany.projektdist.NameServer
U drugim prozorima ste također u src/main/java i pokrenite java com.mycompany.projektdist.ConsensusTester (ime) (redni broj pocevsi od 0) (ukupno procesa)
Kad su svi testeri pokrenuti, algoritam ce pocet, zasad si samo salju poruke i ne rade nista s njima.
