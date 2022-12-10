package me.escordax13.un_economy.commands;

import me.escordax13.un_economy.UN_Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

/*Esta clase será la encargada de operar la lógica de los comandos de nuestro plugin,
* en ella implementaremos la interfaz CommandExecutor*/
public class MenuCommand implements CommandExecutor {

    /*En las siguientes líneas nuestra clase hija (subclase) MenuCommand heredará de
    * la clase padre (superclase) UN_Economy los métodos y atributos de la misma que
    * serán guardados en el campo plugin*/
    private UN_Economy plugin;
    /*El correspondiente constructor para tal acción*/
    public MenuCommand(UN_Economy plugin) {
        this.plugin = plugin;
    }
    /*Este método nos permitirá crear un item similar al usado en el servidor como Billete.
    * El argumento que recibe es un String que almacena la denominación del Billete, esto
    * nos ayudara a que sea más eficiente nuestro código, ya que con un solo método podremos
    * crear todos los Billetes que estén en uso en el servidor */
    public ItemStack billete(String denomination) {

        /*Iniciación variable config en la que obtendremos la información de configuración
        * del servidor que posteriormente atreves de paths obtendremos los strings de los
        * distintos datos que requerimos*/
        FileConfiguration config = plugin.getConfig();
        /*Almacenaremos el nombre del material en la siguiente variable*/
        String itemMaterial = config.getString("Config.Billete" + denomination + ".item_material");
        // cantidad de items que requeriremos
        int amount = 1;
        assert itemMaterial != null;
        /*Si la variable itemMaterial contiene información se creara un item de ese material
        * y de la cantidad de unidades especificadas en la variable amount */
        ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(itemMaterial)), amount);
        /*En la siguiente variable obtendremos la información auxiliar del item como nombre, lore, encantamientos, etc..*/
        ItemMeta meta = item.getItemMeta();

        /*En las siguientes líneas de código, inicializaremos una variable de tipo String con el
        * path (ruta que índica la posición de la información en el archivo de configuración)
        * correspondiente al valor necesitado en cada ocasión, posteriormente verificaremos que la información suministrada
        * por este path sea válida, si es válida se guardara esta información en la variable
        * de tipo String o List<String> correspondiente a la ocasión.
        * La cual traducirá los códigos de color y de estructura del string del archivo de configuración,
        * por último esta información se añadirá a la variable meta con la correspondiente lógica y método para
        *  cada uno de los siguientes casos: nombre, lore y CustomModelData.*/
        String pathname = "Config.Billete" + denomination + ".name";
        String name;
        if (config.contains(pathname)) {
            name = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Config.Billete" + denomination + ".name")));
            assert meta != null;
            meta.setDisplayName(name);

        }

        String pathlore = "Config.Billete" + denomination + ".lore";
        List<String> lore;
        if (config.contains(pathlore)) {
            lore = config.getStringList(pathlore);
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
            }
            assert meta != null;
            meta.setLore(lore);
        }

        String pathCMD = "Config.Billete" + denomination + ".customModelData";
        String numberCMD;
        int numberCustomModelData;
        if (config.contains(pathCMD)) {
            numberCMD = config.getString(pathCMD);
            assert numberCMD != null;
            numberCustomModelData = Integer.parseInt(numberCMD);
            assert meta != null;
            meta.setCustomModelData(numberCustomModelData);
        }

        /*Por última la información Meta almacenada en la variable meta será
        * añadida a la variable item y esta variable meta será la que retorne este método.*/
        item.setItemMeta(meta);

        return item;
    }
    /*El siguiente método nos permitirá realizar la lógica que comprobara si en el
    * inventario del jugador se encuentra el item un item específico que en el servidor hace el rol de un Billete,
    * Si en el inventario del jugador se encuentra dicho item, se retirara únicamente una unidad de ese item y
     * utilizando el siguiente comando: /money give “nombre del jugador” 2 en la consola del servidor
     * se le cargará al saldo económico del jugador el valor del Billete y se le indicará por
     * un mensaje en el chat del juego que su transacción fue exitosa.
     * Si no se encuentra dicho item, se le retornara un mensaje en el chat al jugador indicándole que
     * no posee tal item por lo que no puede ejecutar el comando*/
    public void comprobacionBilleteInventario (ItemStack item, ItemStack[] inventory, Player jugador ){

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].isSimilar(item) && inventory[i].getAmount() >= item.getAmount()) {
                ItemStack removeItem = jugador.getInventory().getItem(i);
                assert removeItem != null;
                int amount = removeItem.getAmount() - 1;
                removeItem.setAmount(amount);
                jugador.getInventory().setItem(i, amount > 0 ? removeItem : null);
                jugador.updateInventory();
                jugador.getServer().dispatchCommand(jugador.getServer().getConsoleSender(), "money give " + jugador.getDisplayName() + " 2");

                jugador.sendMessage(plugin.name + ChatColor.GREEN + "¡Transacción Exitosa!");
                return;
            }
        }
        jugador.sendMessage(plugin.name + ChatColor.RED + "¡Transacción Declinada!");
    }

    /*El siguiente método es propio de Bukkit de la interfaz CommandExecutor, los argumentos que este método recibe son:
    * 1 argumento de tipo Interfaz CommandSender inicializado bajo el identificador sender
    * 2 argumento de tipo Clase Command inicializado bajo el identificador command
    * 3 argumento de tipo String inicializado bajo el identificador label
    * 4 argumento de tipo String List inicializado bajo el identificador args
    * Este método será el encargado de ejecutar la lógica de los distintos comandos que creemos en él.
    * Por último retornará un Boolean indicando el fin de su operación.*/
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*Inicializaremos nuestra variable config de tipo FileConfiguration
        * donde almacenaremos la información de los archivos de configuración.*/
        FileConfiguration config = plugin.getConfig();
        /*Posteriormente, realizaremos una comprobación para que los comandos solo puedan
        * ser ejecutados por una entidad de tipo Player*/
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(plugin.name + ChatColor.LIGHT_PURPLE + " No puedes ejecutar comandos desde la consola");
            return false;
        } else {
            //la variable jugador como su nombre lo índica es en la que almacenaremos la información de la entidad Player.
            Player jugador = (Player) sender;
            /*El siguiente if se encarga de comprobar que la longitud de args sea mayor a 0, si es mayor significa
            * que en el Main ya registramos un comando(UN_Economy). En este caso es /un_economy*/
            if (args.length > 0) {
                /*Ahora este if y los siguientes else if entrarán a revisar
                * el siguiente argumento después de /un_economy y ejecutarán la lógica correspondiente a dicho argumento.*/
                if (args[0].equalsIgnoreCase("version")) {
                    // /un_economy version
                    /*En este caso es un comando que al ser usado le enviara un mensaje al chat del jugador con
                    * la version que está operando en ese momento el plugin*/
                    jugador.sendMessage(plugin.name + ChatColor.LIGHT_PURPLE + "La version del plugin es: " + ChatColor.RED + plugin.version);
                    return true;
                } else if (args[0].equalsIgnoreCase("billete2")) {
                    // /un_economy billete2
                    /*En este caso es un comando que al ser usado comprobara si el usuario posee un Billete de 2 Pesos.
                    * Para tal comprobación se llamará al método Billete() el cual crea un item similar al del Billete del servidor.
                    * Posteriormente, se llamará al método comprobacionBilleteInventario() el cual ejecutara la lógica necesaria
                    * para la revisión que requiere dicho comando del inventario del jugador.
                    * Esta misma lógica y funcionalidad se replicará con los otros 5 items de Billetes*/
                    if (config.contains("Config.Billete2.item_material")) {

                        ItemStack item = billete("2");

                        ItemStack[] inventory = jugador.getInventory().getContents();

                        comprobacionBilleteInventario(item, inventory, jugador);
                    }
                } else if (args[0].equalsIgnoreCase("billete5")) {
                    // /un_economy billete5
                    if (config.contains("Config.Billete5.item_material")) {

                        ItemStack item = billete("5");

                        ItemStack[] inventory = jugador.getInventory().getContents();

                        comprobacionBilleteInventario(item, inventory, jugador);
                    }
                } else if (args[0].equalsIgnoreCase("billete10")) {
                    // /un_economy billete10
                    if (config.contains("Config.Billete10.item_material")) {

                        ItemStack item = billete("10");

                        ItemStack[] inventory = jugador.getInventory().getContents();

                        comprobacionBilleteInventario(item, inventory, jugador);
                    }
                } else if (args[0].equalsIgnoreCase("billete20")) {
                    // /un_economy billete20
                    if (config.contains("Config.Billete20.item_material")) {

                        ItemStack item = billete("20");

                        ItemStack[] inventory = jugador.getInventory().getContents();

                        comprobacionBilleteInventario(item, inventory, jugador);
                    }
                } else if (args[0].equalsIgnoreCase("billete50")) {
                    // /un_economy billete50
                    if (config.contains("Config.Billete50.item_material")) {

                        ItemStack item = billete("50");

                        ItemStack[] inventory = jugador.getInventory().getContents();

                        comprobacionBilleteInventario(item, inventory, jugador);
                    }
                } else if (args[0].equalsIgnoreCase("billete100")) {
                    // /un_economy billete100
                    if (config.contains("Config.Billete100.item_material")) {

                        ItemStack item = billete("100");

                        ItemStack[] inventory = jugador.getInventory().getContents();

                        comprobacionBilleteInventario(item, inventory, jugador);
                    }
                } else if (args[0].equalsIgnoreCase("verf")) {
                    // /un_economy verf
                    /*Este comando lo utilizaremos en caso de que necesitemos conocer la información Meta de un item*/
                    String dataObj = String.valueOf(Objects.requireNonNull(jugador.getInventory().getItem(0)).getItemMeta());
                    jugador.sendMessage(dataObj);
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    // /un_economy reload
                    /*Este comando lo utilizaremos para recargar la información de los archivos configuración del plugin
                    * nos servirá cada vez que el administrador del server modifique alguna información de estos archivos.*/
                    plugin.reloadConfig();
                    jugador.sendMessage(plugin.name + ChatColor.GREEN + "¡Se recargó exitosamente el Plugin!");
                    return true;
                } else {
                    /*Por último si el jugador utiliza un comando que no está creado o una mala syntax al momento de escribir el argumento*/
                    jugador.sendMessage(plugin.name + ChatColor.RED + "ESE COMANDO NO EXISTE!");
                    return true;
                }
            }
        }
        return false;
    }
}