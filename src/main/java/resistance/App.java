package resistance;


public class App 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Running Resistance Player!" );
        
        //ResistancePlayer resistancePlayer = new ResistancePlayer(5, 2);
        ResistanceGame resistancePlayer = new ResistanceGame(3, 1);
        resistancePlayer.determineCombinations(4);
    }
}
