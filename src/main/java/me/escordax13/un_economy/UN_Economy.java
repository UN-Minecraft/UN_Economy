package me.escordax13.un_economy;

import me.escordax13.un_economy.commands.MenuCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UN_Economy extends JavaPlugin {

    /*En las siguientes líneas (16-19) se encontrará la declaración e iniciación de las variables
    * correspondientes a los archivos .yml del que el plugin extrae información de configuración*/

    PluginDescriptionFile pdffile = getDescription();
    public String rutaConfig;
    public String version = pdffile.getVersion();
    public String name = ChatColor.WHITE + "[" + ChatColor.GREEN + pdffile.getName() + ChatColor.WHITE + "]";
    /*El siguiente método se encarga del arranque del plugin, en ella se debera poner los métodos
    * que deseamos sean cargados al inicializar el plugin en el servidor y como buena práctica pondremos
    * un mensaje que se ejecute en la consola del servidor indicando que nuestro plugin se inició correctamente*/
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(name + ChatColor.GREEN + " El Plugin UN_Economy de Escordax13 ha iniciado" + ChatColor.WHITE + "(" + ChatColor.RED +"version: " + ChatColor.RED + version + ChatColor.WHITE +")");
        registrarComandos();
        registerConfig();

    }
    /*El siguiente método se encarga del apagado del plugin, en ella solo debemos poner
    * un mensaje que nos indique en la consola del servidor que el plugin fue finalizado correctamente*/
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(name + ChatColor.WHITE + " El plugin UN_Economy fue apagado (version: " + ChatColor.RED + version + ChatColor.WHITE +")");
    }
    /*El siguiente método se encarga de registrar los comandos de nuestro plugin, en él debemos usar
    * la siguiente línea de código para llevar a cabo tal tarea:
    * this.getCommand("nombre del comando").setExecutor(new Nombre de la clase donde está la lógica de dicho comando (this));*/
    public void registrarComandos() {
        this.getCommand("un_economy").setExecutor(new MenuCommand (this));
    }
    /*El siguiente método se encarga de registrar la información almacenada en los archivos de configuración del plugin,
    * a su vez este método realiza una comprobación que revisa si ya existe una información de configuración previa de
    * ser así no sobreescribe la información de configuración por defecto sobre esta pero si no existe dicha información
    * almacenara la información de configuración por defecto.*/
    public void registerConfig() {
        File config = new File(this.getDataFolder(),"config.yml");
        rutaConfig = config.getPath();
        if(!config.exists()) {
            this.getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }
}
