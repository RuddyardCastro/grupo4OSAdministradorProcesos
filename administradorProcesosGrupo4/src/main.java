/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */





import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;






















/**
 *
 * @author Ruddyard
 */
public class main extends javax.swing.JFrame {
     private DefaultTableModel modelo;
    /**
     * Creates new form main
     */
    public main() {
        initComponents();
        getContentPane().setBackground(Color.WHITE);
        this.setLocationRelativeTo(null);
        No_procesos.setFocusable(false);
        btnOrdenar.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnOrdenarActionPerformed(evt);
        }
    });
    
    btnBuscar.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnBuscarActionPerformed(evt);
        }
    });
    
    
        mostrar_procesos();
    }
    
    
    
    //metodo de grafica Ruddyard 
    public static Map<String, Double> obtenerProcesosDesdeWindows() {
    Map<String, Double> procesos = new LinkedHashMap<>();
    try {
        Process proceso = Runtime.getRuntime().exec("cmd /c tasklist /FO CSV /NH");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] partes = linea.split("\",\"");
            if (partes.length >= 5) {
                String nombre = partes[0].replace("\"", "").trim();
                String memoriaStr = partes[4].replace("\"", "").replace("K", "").replace(",", "").replace(".", "").trim();
                if (!memoriaStr.isEmpty()) {
                    try {
                        double memoriaKB = Double.parseDouble(memoriaStr);
                        double memoriaMB = memoriaKB / 1024;
                        if (memoriaMB > 0) {
                            procesos.put(nombre, memoriaMB);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return procesos;
}
    
    
   
    
    public static Map<String, Double> obtenerProcesosDiscoDesdeWindows() {
    Map<String, Double> procesos = new LinkedHashMap<>();
    try {
        Process proceso = Runtime.getRuntime().exec("cmd /c wmic process get name,readoperationcount,writeoperationcount /format:csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        String linea;
        
        // Saltar encabezados
        reader.readLine();
        
        while ((linea = reader.readLine()) != null) {
            if (linea.trim().isEmpty()) continue;
            
            String[] partes = linea.split(",");
            if (partes.length >= 4) {
                String nombre = partes[1].trim();
                String readOpsStr = partes[2].trim();
                String writeOpsStr = partes[3].trim();
                
                if (!readOpsStr.isEmpty() && !writeOpsStr.isEmpty()) {
                    try {
                        long readOps = Long.parseLong(readOpsStr);
                        long writeOps = Long.parseLong(writeOpsStr);
                        double totalOps = readOps + writeOps;
                        
                        // Convertir operaciones a MB (aproximación)
                        double diskMB = totalOps / 1024.0;
                        
                        if (diskMB > 0) {
                            procesos.put(nombre, diskMB);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return procesos;
}
   
    
    
    
    
    
    
    
    
    
    
    //Gabriela
 private void Alineacion_Columnas() {
        DefaultTableCellRenderer Alinear = new DefaultTableCellRenderer();
        Alinear.setHorizontalAlignment(SwingConstants.RIGHT);
        jtabla_datos.getColumnModel().getColumn(1).setCellRenderer(Alinear);
        jtabla_datos.getColumnModel().getColumn(2).setCellRenderer(Alinear);
        jtabla_datos.getColumnModel().getColumn(3).setCellRenderer(Alinear);
        jtabla_datos.getColumnModel().getColumn(4).setCellRenderer(Alinear);
        jtabla_datos.getColumnModel().getColumn(5).setCellRenderer(Alinear); // CPU
        jtabla_datos.getColumnModel().getColumn(6).setCellRenderer(Alinear); // Disco
        jtabla_datos.getColumnModel().getColumn(7).setCellRenderer(Alinear); //Red
    }
    
    //David Rojas
    //procedimiento de lectura y de insercion de procesos en tabla
    private void mostrar_procesos() {
        int ICol = 0, ICont = 0;
        modelo = (DefaultTableModel) jtabla_datos.getModel();
        Object[] Fila = new Object[8]; //Ahora son 8 columnas
        int i = 0;
        String StrAuxi = "";
        try {
            String line;
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            Set<Integer> procesosConRed = obtenerPIDsDesdeNetstat();
            while ((line = input.readLine()) != null) {
                if (i >= 4) {
                    ICont = 0;
                    String[] sep = line.split("\\s+");
                    while (ICont <= 4) {
                        //String[] sep = line.split("\\s+");
                        if (ICont != 4) {
                            Fila[ICont] = sep[ICont];
                        } else {
                            // convertir Memoria de KB a MB
                            String mem = sep[ICont] + " " + sep[ICont + 1]; 
                            try {
                                mem = mem.replaceAll("[^0-9]", "");
                                if (!mem.isEmpty()) {
                                    long kb = Long.parseLong(mem);
                                    long mb = kb / 1024;
                                    Fila[ICont] = mb + " MB";
                                } else {
                                    Fila[ICont] = "N/A";
                                }
                                long kb = Long.parseLong(mem);
                                long mb = kb / 1024;
                                Fila[ICont] = mb + " MB"; //Memoria en MB
                            } catch (NumberFormatException e) {
                                Fila[ICont] = "N/A"; //Si falla conversión
                            }
                        }
                        ICont++;
                    }

                    // CPU y Disco simulados
                    String nombreProceso = sep[0].toLowerCase(); // Nombre del proceso
                    double base;
                    //colocando un perfil segun el nombre del proceso
                    if (nombreProceso.contains("java") || nombreProceso.contains("netbeans") ||
                        nombreProceso.contains("chrome") || nombreProceso.contains("firefox") ||
                        nombreProceso.contains("code") || nombreProceso.contains("antivirus") ||
                        nombreProceso.contains("teams") || nombreProceso.contains("discord")) {
                        base = 3.5 + Math.random() * 2.5; // Entre 3.5 y 6.0 → perfil alto
                    } else if (nombreProceso.contains("Syst") || nombreProceso.contains("explorer") ||
                            nombreProceso.contains("conhost") || nombreProceso.contains("services") ||
                            nombreProceso.contains("taskhost")) {
                        base = 1.5 + Math.random() * 2.0; // Entre 1.5 y 3.5 → perfil medio
                    } else {
                        base = Math.pow(Math.random(), 2) * 1.5; // Perfil bajo sesgado hacia 0
                    }
                    
                    int pid = -1;
                    if (sep.length > 1) {
                        try {
                            pid = Integer.parseInt(sep[1]);
                        } catch (NumberFormatException e) {
                            pid = -1; // Valor inválido
                        }
                    }

                    perfilCPU.put(pid, base);
                    Fila[5] = String.format("%.1f %%", base);

                    double discoSesgado = Math.pow(Math.random(), 2.5) * 1.0; // Sesgado hacia valores bajos
                    Fila[6] = String.format("%.2f MB/s", discoSesgado);


                    
                    if (pid != -1 && procesosConRed.contains(pid)) {
                        double redSimulada = 0.01 + Math.random() * (5.00 - 0.01); // Entre 0.01 y 5.00 Mbps
                        Fila[7] = String.format("%.2f Mbps", redSimulada);
                    } else {
                        Fila[7] = "0.00 Mbps";
                    }

                    modelo.addRow(Fila);
                    jtabla_datos.setModel(modelo);
                }
                i++;
            }
            input.close();
            Alineacion_Columnas();
            for (int j = 0; j < jtabla_datos.getColumnCount(); j++) {
                jtabla_datos.getColumnModel().getColumn(j).setCellRenderer(getColorRenderer());
            }
            No_procesos.setText(String.valueOf(i));
            iniciarActualizacionRed();
            iniciarActualizacionMemoria();
            iniciarActualizacionCPU();
            iniciarActualizacionDisco();
        } catch (Exception err) {
            err.printStackTrace();
        }

    }
    //Anderson Rodriguez
    //Metodo que sirve para actualizar cada segundo el uso de memoria de los procesos listados en la tabla
    //Obtiene la información directamente del comando `tasklist.exe` de Windows
    private void iniciarActualizacionMemoria() {
        // Crea un temporizador que ejecuta la tarea cada 1000 ms (1 segundo)
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Ejecuta el comando tasklist para obtener la lista de procesos
                    Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    Map<Integer, String> memoriaPorPID = new HashMap<>();

                    int i = 0;
                    while ((line = input.readLine()) != null) {
                        // Ignora las primeras 4 líneas del encabezado
                        if (i >= 4) {
                            String[] sep = line.trim().split("\\s+");
                            if (sep.length >= 6) {
                                try {
                                    // Extrae el PID y la memoria usada
                                    int pid = Integer.parseInt(sep[1]);
                                    String mem = sep[4] + " " + sep[5];
                                    mem = mem.replace("KB", "").replace(",", "").trim();
                                    long kb = Long.parseLong(mem);
                                    long mb = kb / 1024;
                                    memoriaPorPID.put(pid, mb + " MB");
                                } catch (NumberFormatException ex) {
                                    // Ignorar si no se puede convertir
                                }
                            }
                        }
                        i++;
                    }
                    input.close();

                    // Actualiza la columna de memoria en la tabla para cada proceso
                    for (int fila = 0; fila < jtabla_datos.getRowCount(); fila++) {
                        Object pidObj = jtabla_datos.getValueAt(fila, 1); // PID está en la columna 1
                        int pid = -1;
                        try {
                            pid = Integer.parseInt(pidObj.toString());
                        } catch (NumberFormatException ex) {
                            pid = -1;
                        }

                        if (pid != -1 && memoriaPorPID.containsKey(pid)) {
                            jtabla_datos.setValueAt(memoriaPorPID.get(pid), fila, 4); // Columna 4 = Memoria
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        timer.start();
    }
    
    private Map<Integer, Double> perfilCPU = new HashMap<>();
    //Anderson Rodriguez
    //Método que simula cada segundo el uso de CPU de los procesos en la tabla
    //Utiliza una variación aleatoria basada en un perfil base por PID
    private void iniciarActualizacionCPU() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int fila = 0; fila < jtabla_datos.getRowCount(); fila++) {
                    Object pidObj = jtabla_datos.getValueAt(fila, 1); // PID en columna 1
                    int pid = -1;
                    try {
                        pid = Integer.parseInt(pidObj.toString());
                    } catch (NumberFormatException ex) {
                        continue;
                    }
                    double base = perfilCPU.getOrDefault(pid, 0.5); // Valor por defecto si no existe
                    double variacion = (Math.random() - 0.5) * 1.0; // Fluctúa entre -0.5 y +0.5
                    double nuevoValor = Math.max(0.0, Math.min(6.0, base + variacion)); // Limita entre 0 y 6
                    // Actualiza la columna de CPU en la tabla
                    jtabla_datos.setValueAt(String.format("%.1f %%", nuevoValor), fila, 5);
                }
            }
        });
        timer.start();
    }
    //Anderson Rodriguez
    //Metodo que simula cada segundo el uso de disco por proceso.
    //Genera valores sesgados hacia cifras bajas (entre 0.0 y 1.0 MB/s).
    private void iniciarActualizacionDisco() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int fila = 0; fila < jtabla_datos.getRowCount(); fila++) {
                    // Simulación sesgada hacia valores bajos entre 0.0 y 1.0 MB/s
                    double discoSesgado = Math.pow(Math.random(), 2.5) * 1.0;
                    String discoTexto = String.format("%.2f MB/s", discoSesgado);
                    // Actualiza la columna de Disco en la tabla
                    jtabla_datos.setValueAt(discoTexto, fila, 6); // Columna 6 = Disco
                }
            }
        });
        timer.start();
    }

    
    
    //Anderson Rodriguez
    /* Metodo que devuelve un renderizador personalizado para celdas de la tabla.Este renderizador 
    aplica colores de fondo según el valor de uso de recursos (Memoria, CPU, Disco, Red).
    Los colores varían en intensidad para destacar valores altos. */
    private DefaultTableCellRenderer getColorRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                
                if (isSelected) {
                    // Si la fila está seleccionada, usar los colores de selección por defecto
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                    return c;
                }
                //Columna 4: Uso de Memoria
                if (column == 4){ // Solo aplicar estilo si es la columna de uso de memoria
                    String usoMemoriaStr = value.toString().replace("MB", "").replace(",", "").trim();

                    try {
                        int usoMemoria = Integer.parseInt(usoMemoriaStr);
                        if (usoMemoria > 100) { // Umbral: 100 MB
                            c.setBackground(new Color(70, 130, 180)); // azul fuerte
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(new Color(173, 216, 230));
                            c.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                    
                // CPU (columna 5)
                else if (column == 5) {
                    String usoCPUStr = value.toString().replace("%", "").replace(",", "").trim();
                    try {
                        double usoCPU = Double.parseDouble(usoCPUStr);
                        if (usoCPU >= 1.0) {
                            c.setBackground(new Color(70, 130, 180)); // azul fuerte 
                        } else {
                            c.setBackground(new Color(173, 216, 230)); // azul claro 
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                // Disco (columna 6)
                else if (column == 6) {
                    String usoDiscoStr = value.toString().replace("MB/s", "").replace(",", "").trim();
                    try {
                        double usoDisco = Double.parseDouble(usoDiscoStr);
                        if (usoDisco >= 0.8) {
                            c.setBackground(new Color(70, 130, 180)); // azul fuerte
                        } else {
                            c.setBackground(new Color(173, 216, 230)); // azul claro
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                
                 // Red (columna 7)
                else if (column == 7) {
                    String usoRedStr = value.toString().replace("Mbps", "").replace(",", "").trim();
                    try {
                        double usoRed = Double.parseDouble(usoRedStr);
                        if (usoRed >= 3.0) {
                            c.setBackground(new Color(70, 130, 180)); // azul fuerte
                        } else {
                            c.setBackground(new Color(173, 216, 230)); // azul claro
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                else {
                    // Para otras columnas, mantener estilo normal
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                

                return c;
            }
        };
    }
    //Anderson Rodriguez
    //Metodo que actualiza cada segundo el uso de red de los procesos en la tabla.
    //Simula tráfico de red solo para procesos que tienen conexiones activas (según netstat).
    private void iniciarActualizacionRed() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<Integer> procesosConRed = obtenerPIDsDesdeNetstat(); // Actualizar PIDs con conexión activa

                for (int fila = 0; fila < jtabla_datos.getRowCount(); fila++) {
                    Object pidObj = jtabla_datos.getValueAt(fila, 1); // PID está en la columna 1
                    int pid = -1;
                    try {
                        pid = Integer.parseInt(pidObj.toString());
                    } catch (NumberFormatException ex) {
                        pid = -1;
                    }
                    // Si el proceso tiene conexión activa, simula tráfico entre 0.01 y 5.00 Mbps
                    if (pid != -1 && procesosConRed.contains(pid)) {
                        double redSimulada = 0.01 + Math.random() * (5.00 - 0.01);
                        jtabla_datos.setValueAt(String.format("%.2f Mbps", redSimulada), fila, 7); // Columna 7 = Red
                    } else {
                        jtabla_datos.setValueAt("0.00 Mbps", fila, 7);
                    }
                }
            }
        });
        timer.start();
    }
    //Anderson Rodriguez
    /*método que jecuta el comando `netstat -ano` para obtener los PIDs de procesos con conexiones activas.
    Filtra líneas que comienzan con TCP o UDP y extrae el PID del final de cada línea y retorna el
    conjunto de PIDs con actividad de red.*/
    private Set<Integer> obtenerPIDsDesdeNetstat() {
        Set<Integer> pids = new HashSet<>();
        try {
            Process proceso = Runtime.getRuntime().exec("netstat -ano");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.startsWith("TCP") || linea.startsWith("UDP")) {
                    String[] partes = linea.split("\\s+");
                    if (partes.length >= 5) {
                        try {
                            int pid = Integer.parseInt(partes[partes.length - 1]);
                            pids.add(pid);
                        } catch (NumberFormatException e) {
                            // Ignorar líneas con PID no válido
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pids;
    }
    
    //Ruddyard Castro 9959-23-1409
    // procedimiento de limpieza de la tabla la restablece de a los parametros inisciales
 // Método para limpiar la tabla y restablecerla a su estado inicial
void LimpiarTabla() {
    // Establecer un nuevo modelo de tabla con las columnas definidas
    jtabla_datos.setModel(new javax.swing.table.DefaultTableModel(
            // Datos iniciales vacíos
            new Object[][]{},
            // Nombres de las columnas
            new String[]{
                "Nombre", "PID", "Tipo de sesión", "Número de sesión", 
                "Uso de memoria", "CPU (%)", "Disco (MB/s)", "Red (%)"
            }
    ) {
        // Array que define qué columnas son editables (todas false en este caso)
        boolean[] canEdit = new boolean[]{
            false, false, false, false, false, false, false, false
        };

        // Método que determina si una celda es editable
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }
    });
}

// Método para matar un proceso seleccionado en la tabla
// Ruddyard Castro 9959-23-1409
public void Matar_proceso() {
    // Obtener el modelo de la tabla para acceder a los datos
    modelo = (DefaultTableModel) jtabla_datos.getModel();
    // Obtener el valor de la primera columna (Nombre del proceso) de la fila seleccionada
    String StrCelda = String.valueOf(modelo.getValueAt(jtabla_datos.getSelectedRow(), 0));
    
    // Verificar si no se ha seleccionado ningún proceso o el campo está vacío
    if (StrCelda == "") {
        // Mostrar mensaje de error al usuario
        JOptionPane.showMessageDialog(null, "ERROR, No se ha seleccionado ningún proceso", "Error", JOptionPane.INFORMATION_MESSAGE);
    } else {
        try {
            // Crear un proceso para ejecutar el comando de terminación de tareas de Windows
            Process hijo;
            // Ejecutar el comando taskkill para forzar la terminación del proceso
            hijo = Runtime.getRuntime().exec("taskkill /F /IM " + StrCelda);
            // Esperar a que el comando termine de ejecutarse
            hijo.waitFor();
        } catch (IOException | InterruptedException ex) {
            // Registrar cualquier error que ocurra durante la ejecución
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
private Map<String, String> obtenerInformacionSistema() {
    Map<String, String> info = new HashMap<>();

    try {
        // Nombre del dispositivo 
        info.put("DeviceName", System.getenv("COMPUTERNAME"));

        // Procesador
        info.put("Processor", ejecutarComando("wmic cpu get name", "Name"));
        // CPU Detalles adicionales
info.put("Cores", ejecutarComando("wmic cpu get NumberOfCores", "NumberOfCores"));
info.put("LogicalProcessors", ejecutarComando("wmic cpu get NumberOfLogicalProcessors", "NumberOfLogicalProcessors"));
info.put("BaseSpeed (MHz)", ejecutarComando("wmic cpu get MaxClockSpeed", "MaxClockSpeed") + " MHz");
info.put("L2 Cache", ejecutarComando("wmic cpu get L2CacheSize", "L2CacheSize") + " KB");
info.put("L3 Cache", ejecutarComando("wmic cpu get L3CacheSize", "L3CacheSize") + " KB");

        // RAM
        String ramStr = ejecutarComando("wmic computersystem get totalphysicalmemory", "TotalPhysicalMemory");
        if (!ramStr.isEmpty()) {
            try {
                long totalMemoryBytes = Long.parseLong(ramStr);
                double totalMemoryGB = totalMemoryBytes / (1024.0 * 1024.0 * 1024.0);
                info.put("RAM", String.format("%.1f GB", totalMemoryGB));
            } catch (NumberFormatException e) {
                info.put("RAM", "N/A");
            }
        }
// Disco principal
        String diskSize = ejecutarComando("wmic diskdrive get Size", "Size");
        if (!diskSize.isEmpty()) {
            try {
                long sizeBytes = Long.parseLong(diskSize);
                double sizeGB = sizeBytes / (1024.0 * 1024.0 * 1024.0);
                info.put("Disk Capacity", String.format("%.0f GB", sizeGB));
            } catch (NumberFormatException e) {
                info.put("Disk Capacity", "N/A");
            }
        }
        info.put("Disk Type", ejecutarComando("wmic diskdrive get MediaType", "MediaType"));
        // Device ID
        info.put("DeviceID", ejecutarComando("wmic csproduct get uuid", "UUID"));

        // Tipo de sistema (arquitectura real)
        info.put("SystemType", System.getProperty("os.arch"));

    } catch (Exception e) {
        info.put("Error", "No se pudo obtener información del sistema: " + e.getMessage());
    }

    return info;
}

// Método auxiliar para ejecutar comandos y filtrar encabezados
private String ejecutarComando(String comando, String encabezado) {
    try {
        Process process = Runtime.getRuntime().exec(comando);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty() && !line.contains(encabezado)) {
                reader.close();
                return line.trim();
            }
        }
        reader.close();
    } catch (Exception ignored) {}
    return "";
}

      /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtabla_datos = new javax.swing.JTable();
        jIniciar_procesos = new javax.swing.JButton();
        btnSuspenderProceso = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        No_procesos = new javax.swing.JTextField();
        btnEspesifiaciones = new javax.swing.JButton();
        btnOrdenar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnOrdenarMenor = new javax.swing.JButton();
        btnGraficaMem = new javax.swing.JButton();
        btnGraficaDisco = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jtabla_datos.setBackground(new java.awt.Color(255, 236, 194));
        jtabla_datos.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jtabla_datos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "PID", "Tipo de sesión ", "Número de sesión", "Uso de memoria", "CPU (%)", "Disco (MB/s)", "Red (%)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jtabla_datos);

        jIniciar_procesos.setText("PROCESOS");
        jIniciar_procesos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jIniciar_procesosActionPerformed(evt);
            }
        });

        btnSuspenderProceso.setText("Suspender Proceso");
        btnSuspenderProceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuspenderProcesoActionPerformed(evt);
            }
        });

        jLabel2.setText("TOTAL DE PROCESOS: ");

        No_procesos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                No_procesosActionPerformed(evt);
            }
        });

        btnEspesifiaciones.setText("Espesificaciones");
        btnEspesifiaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEspesifiacionesActionPerformed(evt);
            }
        });

        btnOrdenar.setText("Filtrar Orden");

        btnBuscar.setText("Buscar");

        btnOrdenarMenor.setText("Filtrar Orden Menor ");
        btnOrdenarMenor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdenarMenorActionPerformed(evt);
            }
        });

        btnGraficaMem.setText("Grafica  Memoria");
        btnGraficaMem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraficaMemActionPerformed(evt);
            }
        });

        btnGraficaDisco.setText("Grafica  Disco");
        btnGraficaDisco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraficaDiscoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOrdenar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOrdenarMenor)
                        .addGap(44, 44, 44)
                        .addComponent(btnGraficaMem)
                        .addGap(18, 18, 18)
                        .addComponent(btnGraficaDisco))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(No_procesos, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jIniciar_procesos, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSuspenderProceso, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btnEspesifiaciones)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(No_procesos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jIniciar_procesos)
                    .addComponent(btnSuspenderProceso)
                    .addComponent(btnEspesifiaciones))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnOrdenar)
                            .addComponent(btnBuscar)
                            .addComponent(btnOrdenarMenor)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGraficaMem))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGraficaDisco)))
                .addGap(96, 96, 96))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jIniciar_procesosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jIniciar_procesosActionPerformed

        LimpiarTabla();//limpia la tabla antes de insertr todos los procesos
        mostrar_procesos();//llama al procedimiento de mostrar procesos y los coloca en la tabla

    }//GEN-LAST:event_jIniciar_procesosActionPerformed

    private void btnSuspenderProcesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuspenderProcesoActionPerformed
//llama al procedimiento de terminar un proceso
        Matar_proceso();
     //limpia la tabla antes de colocar los procesos despues de haber terminado uno   
        LimpiarTabla();
        //coloca de nuevo los procesos que quedaron sin los que se acaban de terminar
        mostrar_procesos();
    }//GEN-LAST:event_btnSuspenderProcesoActionPerformed

    private void No_procesosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_No_procesosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_No_procesosActionPerformed

    private void btnEspesifiacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEspesifiacionesActionPerformed
        // TODO add your handling code here:
        
         Map<String, String> info = obtenerInformacionSistema();

    // Convertir el mapa en un String bonito
    StringBuilder sb = new StringBuilder("Información del sistema:\n\n");
    info.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));

    // Mostrar en un JOptionPane
    JOptionPane.showMessageDialog(this, sb.toString(), "Especificaciones", JOptionPane.INFORMATION_MESSAGE);
        
    }//GEN-LAST:event_btnEspesifiacionesActionPerformed

    private void btnOrdenarMenorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdenarMenorActionPerformed
        // TODO add your handling code here:
        // Ruddayrd castro 
        DefaultTableModel model = (DefaultTableModel) jtabla_datos.getModel();

    // Verificar que hay datos
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay datos para ordenar");
        return;
    }

    java.util.List<Object[]> datos = new java.util.ArrayList<>();
    for (int i = 0; i < model.getRowCount(); i++) {
        Object[] fila = new Object[model.getColumnCount()];
        for (int j = 0; j < model.getColumnCount(); j++) {
            fila[j] = model.getValueAt(i, j);
        }
        datos.add(fila);
    }

    // Ordenar de menor a mayor uso de memoria
    datos.sort((a, b) -> {
        try {
            // Manejar valores nulos
            if (a[4] == null || b[4] == null) {
                return a[4] == null ? -1 : 1; // Los nulos van primero
            }
            
            int memA = extraerMB(a[4].toString());
            int memB = extraerMB(b[4].toString());

            if (memA != memB) {
                return Integer.compare(memA, memB); // Menor a mayor
            }
            
            // Si la memoria es igual, orden alfabético por Nombre
            if (a[0] == null || b[0] == null) {
                return a[0] == null ? -1 : 1;
            }
            return a[0].toString().compareToIgnoreCase(b[0].toString());
            
        } catch (Exception e) {
            return 0; // En caso de error, mantener orden actual
        }
    });

    // Limpiar y volver a insertar
    model.setRowCount(0);
    for (Object[] fila : datos) {
        model.addRow(fila);
    }
    }//GEN-LAST:event_btnOrdenarMenorActionPerformed

    private void btnGraficaMemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraficaMemActionPerformed
        // TODO add your handling code here:
        // Ruddyard Castro 9959-59-23-1409
        
        // Crear la ventana principal para la distribución de memoria
JFrame ventana = new JFrame("Distribución de Memoria");
// Configurar el tamaño de la ventana (ancho x alto)
ventana.setSize(700, 600);
// Centrar la ventana en la pantalla
ventana.setLocationRelativeTo(null);
// Definir que al cerrar solo se cierre esta ventana, no toda la aplicación
ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

// Crear un panel personalizado para dibujar la gráfica circular de memoria
JPanel panelGrafica = new JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
        // Llamar al método padre para el renderizado básico
        super.paintComponent(g);
        // Convertir a Graphics2D para funcionalidades avanzadas de dibujo
        Graphics2D g2 = (Graphics2D) g;
        // Activar antialiasing para bordes suavizados
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Obtener procesos reales de memoria desde Windows
        Map<String, Double> procesos = obtenerProcesosDesdeWindows();

        // Si no hay datos reales, usar datos simulados para evitar gráfica vacía
        if (procesos.isEmpty()) {
            procesos = new LinkedHashMap<>();
            procesos.put("operaApp.exe", 320.0);
            procesos.put("FChrome.exe", 210.0);
            procesos.put("TestJava.exe", 180.0);
            procesos.put("DummyExplorer.exe", 95.0);
            procesos.put("FakeService.exe", 60.0);
        }

        // Obtener los 10 procesos que más memoria consumen
        List<Map.Entry<String, Double>> topProcesos = procesos.entrySet().stream()
                // Ordenar de mayor a menor consumo de memoria
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                // Limitar a los 10 primeros
                .limit(10)
                // Convertir a lista
                .toList();

        // Calcular el total de memoria consumida por los procesos top
        double total = topProcesos.stream().mapToDouble(Map.Entry::getValue).sum();

        // Definir posición y dimensiones de la gráfica circular
        int x = 150, y = 100, ancho = 300, alto = 300;
        // Ángulo inicial para comenzar a dibujar
        double anguloInicio = 0;
        // Contador para asignar colores diferentes
        int i = 0;

        // Dibujar cada segmento de la gráfica circular
        for (Map.Entry<String, Double> entry : topProcesos) {
            // Calcular el porcentaje de memoria que usa este proceso
            double porcentaje = entry.getValue() / total;
            // Convertir porcentaje a ángulo (360 grados = 100%)
            double angulo = porcentaje * 360;

            // Asignar color diferente a cada segmento usando modelo HSB
            g2.setColor(Color.getHSBColor((float) i / topProcesos.size(), 0.7f, 0.9f));
            // Dibujar el segmento circular (arco relleno)
            g2.fillArc(x, y, ancho, alto, (int) anguloInicio, (int) angulo);
            // Actualizar ángulo de inicio para el próximo segmento
            anguloInicio += angulo;
            // Incrementar contador de colores
            i++;
        }

        // Dibujar la leyenda de la gráfica
        int leyendaY = 420;  // Posición vertical inicial de la leyenda
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));  // Configurar fuente del texto
        i = 0;  // Reiniciar contador para colores
        for (Map.Entry<String, Double> entry : topProcesos) {
            // Dibujar el cuadro de color de la leyenda
            g2.setColor(Color.getHSBColor((float) i / topProcesos.size(), 0.7f, 0.9f));
            g2.fillRect(30, leyendaY, 15, 15);
            // Dibujar el texto de la leyenda (nombre del proceso y consumo en MB)
            g2.setColor(Color.BLACK);
            g2.drawString(entry.getKey() + String.format(" (%.1f MB)", entry.getValue()), 50, leyendaY + 12);
            // Mover a la siguiente línea de la leyenda
            leyendaY += 20;
            i++;
        }
    }
};

// Agregar el panel de la gráfica a la ventana
ventana.add(panelGrafica);
// Hacer visible la ventana
ventana.setVisible(true);
        
    }//GEN-LAST:event_btnGraficaMemActionPerformed
//Ruddyard cAstro 
    private void btnGraficaDiscoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraficaDiscoActionPerformed
        // TODO add your handling code here:
    
        // Crear la ventana principal de la aplicación
        JFrame ventana = new JFrame("Distribución de Uso de Disco");
// Configurar el tamaño de la ventana (ancho, alto)
        ventana.setSize(700, 600);
// Centrar la ventana en la pantalla
        ventana.setLocationRelativeTo(null);
// Definir el comportamiento al cerrar la ventana (solo cerrar esta ventana)
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

// Crear un panel personalizado para dibujar la gráfica circular
        JPanel panelGrafica = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Llamar al método de la clase padre para asegurar el renderizado correcto
                super.paintComponent(g);
                // Convertir Graphics a Graphics2D para usar funcionalidades avanzadas
                Graphics2D g2 = (Graphics2D) g;
                // Activar antialiasing para suavizar los bordes de los gráficos
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Obtener los procesos de disco desde Windows (método externo)
                Map<String, Double> procesos = obtenerProcesosDiscoDesdeWindows();

                // Si no se obtuvieron procesos, usar datos de ejemplo para demostración
                if (procesos.isEmpty()) {
                    procesos = new LinkedHashMap<>();
                    procesos.put("chrome.exe", 15.5);
                    procesos.put("sqlservr.exe", 8.2);
                    procesos.put("javaw.exe", 5.7);
                    procesos.put("explorer.exe", 3.1);
                    procesos.put("svchost.exe", 2.8);
                }

                // Obtener los 10 procesos que más uso de disco tienen
                List<Map.Entry<String, Double>> topProcesos = procesos.entrySet().stream()
                        // Ordenar de mayor a menor por valor de uso de disco
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        // Limitar a los 10 primeros
                        .limit(10)
                        // Convertir a lista
                        .toList();

                // Calcular el total de uso de disco para calcular porcentajes
                double total = topProcesos.stream().mapToDouble(Map.Entry::getValue).sum();

                // Definir las dimensiones y posición de la gráfica circular
                int x = 150, y = 100, ancho = 300, alto = 300;
                // Ángulo inicial para comenzar a dibujar la gráfica
                double anguloInicio = 0;
                // Contador para asignar colores diferentes a cada segmento
                int i = 0;

                // Dibujar cada segmento de la gráfica circular
                for (Map.Entry<String, Double> entry : topProcesos) {
                    // Calcular el porcentaje que representa este proceso del total
                    double porcentaje = entry.getValue() / total;
                    // Convertir el porcentaje a ángulo (360 grados = 100%)
                    double angulo = porcentaje * 360;

                    // Asignar un color diferente a cada segmento usando HSB
                    g2.setColor(Color.getHSBColor((float) i / topProcesos.size(), 0.7f, 0.9f));
                    // Dibujar el segmento circular (arco relleno)
                    g2.fillArc(x, y, ancho, alto, (int) anguloInicio, (int) angulo);
                    // Actualizar el ángulo de inicio para el próximo segmento
                    anguloInicio += angulo;
                    // Incrementar el contador de colores
                    i++;
                }

                // Dibujar la leyenda de la gráfica
                int leyendaY = 420;  // Posición vertical inicial de la leyenda
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));  // Fuente para el texto
                i = 0;  // Reiniciar contador para colores
                for (Map.Entry<String, Double> entry : topProcesos) {
                    // Dibujar el cuadro de color de la leyenda
                    g2.setColor(Color.getHSBColor((float) i / topProcesos.size(), 0.7f, 0.9f));
                    g2.fillRect(30, leyendaY, 15, 15);
                    // Dibujar el texto de la leyenda (nombre del proceso y uso en MB/s)
                    g2.setColor(Color.BLACK);
                    g2.drawString(entry.getKey() + String.format(" (%.2f MB/s)", entry.getValue()), 50, leyendaY + 12);
                    // Mover a la siguiente línea de la leyenda
                    leyendaY += 20;
                    i++;
                }
            }
};

ventana.add(panelGrafica);
ventana.setVisible(true);
    }//GEN-LAST:event_btnGraficaDiscoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main().setVisible(true);
            }
        });
    }
    
    //David Rojas
    //Ordenar de mayor a menor en orden Alfabetico
    private void btnOrdenarActionPerformed(java.awt.event.ActionEvent evt) {
    DefaultTableModel model = (DefaultTableModel) jtabla_datos.getModel();

    java.util.List<Object[]> datos = new java.util.ArrayList<>();
    for (int i = 0; i < model.getRowCount(); i++) {
        Object[] fila = new Object[model.getColumnCount()];
        for (int j = 0; j < model.getColumnCount(); j++) {
            fila[j] = model.getValueAt(i, j);
        }
        datos.add(fila);
    }

    datos.sort((a, b) -> {
        // Columna 4 = Uso de memoria (ej: "123 MB")
        int memA = extraerMB(a[4].toString());
        int memB = extraerMB(b[4].toString());

        if (memB != memA) {
            return Integer.compare(memB, memA); // Mayor a menor
        }
        // Si la memoria es igual, orden alfabético por Nombre (columna 0)
        return a[0].toString().compareToIgnoreCase(b[0].toString());
    });

    // Limpiar y volver a insertar
    model.setRowCount(0);
    for (Object[] fila : datos) {
        model.addRow(fila);
    }
}

    // Método auxiliar para extraer solo el número en MB
    private int extraerMB(String texto) {
    try {
        return Integer.parseInt(texto.replaceAll("[^0-9]", ""));
    } catch (NumberFormatException e) {
        return 0;
    }
}

    //David Rojas
    //Opcion de busqueda
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {
    String texto = JOptionPane.showInputDialog(this, "Ingrese texto a buscar en Nombre de proceso:");
    if (texto == null || texto.trim().isEmpty()) return;

    texto = texto.toLowerCase();
    jtabla_datos.clearSelection();

    for (int i = 0; i < jtabla_datos.getRowCount(); i++) {
        String nombre = jtabla_datos.getValueAt(i, 0).toString().toLowerCase();
        if (nombre.contains(texto)) {
            // Selecciona la fila encontrada
            jtabla_datos.addRowSelectionInterval(i, i);
        }
    }
}
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField No_procesos;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEspesifiaciones;
    private javax.swing.JButton btnGraficaDisco;
    private javax.swing.JButton btnGraficaMem;
    private javax.swing.JButton btnOrdenar;
    private javax.swing.JButton btnOrdenarMenor;
    private javax.swing.JButton btnSuspenderProceso;
    private javax.swing.JButton jIniciar_procesos;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtabla_datos;
    // End of variables declaration//GEN-END:variables
}
