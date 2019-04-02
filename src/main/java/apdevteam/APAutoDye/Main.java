/**
 * Original code written in Java 8 by Foxtrot2400
 * Credit to TylerS1066 and Exswordion
 * Tested in Spigot 1.10.2
 */

package ap.apautodye.apdevteam; //Package name - Do not change unless you change the path


import net.milkbowl.vault.permission.Permission; //When compiling **MAKE SURE** you have vault in your modules or this will error
import org.bukkit.Material; // This is the basis of the entire plugin. If you are updating this to a newer version, something probably will need changed with this.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player; // Player stuff
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor; // Pretty chat colors for our output
import java.util.logging.Logger; //Java Logger


public class Main extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft"); //Sets up our logger for basic feedback to the console
    private static Permission perms;  //Set up perms with vault
    private String[] Colors = {"white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black"}; //List of colors of wool (in order). Array item 0 is wool color white and also wool sub ID 0
    private String[] ColorCodes = {"f", "6", "5", "9", "e", "a", "d", "8", "7", "b", "5", "1", "6", "2", "c", "0"}; // List of matching colors for our wool ID. These are the color codes that bukkit uses after the & for each of our above colors, in the same order as above.
    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion())); //Notify the console the plugin is disabled
    }

    @Override
    public void onEnable() {
        log.info(String.format("[%s] v%s initializing.", getDescription().getName(), getDescription().getVersion()));   //Notify the console the plugin has started up
        setupPermissions(); // Set up our basic permission parameters
    }

    private boolean setupPermissions() { //Set up basic permission parameters
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class); //Sets up our perms provider
        perms = rsp.getProvider(); //Creates our variable for our permission provider
        return perms != null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dye")) { //This is our only command for the plugin
            byte x = 0; //This is our basic counter. Its set to a byte to use as little space as possible, but it doesn't really matter.
            byte ColorID = 20; //This is our color ID variable. It is purposefully set *out of range* so that if someone enters something like "blurple" and it's not recognized by our string match, it will report that an invalid color was entered.
            Player player = null; // Setup our player
            String colorString = ""; //Setup color string
            String changeColor = ""; //Setup our color change string
            String colorOutput = ""; //Output variable that looks pretty, so that "lightgray" looks like "light gray"

            if (sender instanceof Player) {
                player = (Player) sender;          //Initialize who the target player is
            } else {
                sendMessage(sender,"This command must be run by a player!"); // If it isn't a player using this command, tell them they can't
                return true;
            }
            if(args[0].equalsIgnoreCase("light")){ // We know that the only time there will be two words is if there's "light" before the wool color. So, if "light" is the first argument, we know we need to use two arguments rather than one for "light gray" or whatever
                x = 0; //Reset our counter to zero...just in case.
                colorString = args[0]+args[1]; //Setup our string to use both arguments needed
                colorOutput = args[0]+" "+args[1]; //Setup our output string that looks pretty
                for(String testString : Colors){
                    ++x;
                    if(testString.equals(colorString.toLowerCase())){
                        ColorID = (byte)(x-1);
                        changeColor = colorOutput; //This just goes through until it finds a match, and sets the color of the wool to whatever text matched (offset 1 to the left because that actually works right)
                    }
                }
            }
            else{
                x = 0;
                colorString = args[0];
                for(String testString : Colors){
                    ++x; //Reset our counter to zero...just in case.
                    if(testString.equals(colorString.toLowerCase())){
                        ColorID = (byte)(x - 1);
                        changeColor = testString; //Same thing, just slightly less complex. Find the match, and set the output string to something pretty.
                    }
                }
            }
            try {
                ColorID = Byte.parseByte(args[0]); //This is our last case, and most unlikely. If the player enters something like /dye 15, this will allow us to know that's actually black wool, and change it accordingly
                changeColor = Colors[ColorID]; //We can also just access the color ID because this is already a number (woo!)
            }
            catch(Exception e){
            } // if it errors, its probably because its not a number so move on
            if (ColorID >= 0 & ColorID <= 15) {
                for (x = 0; x <= 15; x++) { //Due to different colors having slightly different IDs, we have to iterate through this 15 times to check for each color
                    ItemStack wool = new ItemStack(Material.WOOL, 1, (byte) x);
                    ItemStack clay = new ItemStack(Material.STAINED_CLAY, 1, (byte) x); //Create our item types for wool and clay
                    if (player.getInventory().getItemInMainHand().isSimilar(wool)) { //isSimiliar checks if it is the same item type (exact to color), but doesn't care about how many are there
                        byte woolAmount = (byte) player.getInventory().getItemInMainHand().getAmount(); //Find out how much wool the player is holding
                        ItemStack woolColor = new ItemStack(Material.WOOL, woolAmount, ColorID); //Create the new wool type that we need to give the player as a replacement
                        player.getInventory().setItemInMainHand(woolColor); //Set the current wool to our new dyed stack
                        sendMessage(sender, String.format("Wool color changed to &%s%s&6!", ColorCodes[ColorID], changeColor)); //Output our wool color that we changed to
                        return true;
                    }
                    if (player.getInventory().getItemInMainHand().isSimilar(clay)) {
                        byte clayAmount = (byte) player.getInventory().getItemInMainHand().getAmount(); //Find out how much clay the player is holding
                        ItemStack clayColor = new ItemStack(Material.STAINED_CLAY, clayAmount, ColorID); // Create the new clay type that we need to give the player as a replacement
                        player.getInventory().setItemInMainHand(clayColor); //Set the current clay to our new dyed stack
                        sendMessage(sender, String.format("Clay color changed to &%s%s&6!", ColorCodes[ColorID], changeColor)); //Output our clay color that we changed to
                        return true;
                    }

                }

            }
            else{
                sendMessage(sender, "Please use a valid color or color ID"); //If nothing was found as a result of our lookup, tell the player they done messed up
                return true;
            }
            sendMessage(sender, "You must be holding wool or clay to run this command."); //If the player isn't actually holding clay or wool, tell them that they should be
            return true;
        }
        return false;

    }

    private void sendMessage(CommandSender sender, String msg){ //Send message to either player or console, strings only (no lists)
        if(sender instanceof Player){ //If the sender is a player
            Player p = (Player) sender; //Initialize the player
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',ChatColor.GREEN+"[APAutoDye] " + ChatColor.GOLD + msg)); //Send the message intended for the player
        }
        else //If the sender isn't a player, it must be the console requesting the info
        {
            log.info("[APAutoDye] " + msg); //Send the message to the console without fancy formatting
        }
    }
}