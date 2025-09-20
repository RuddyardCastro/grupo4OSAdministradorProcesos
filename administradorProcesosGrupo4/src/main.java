/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */





import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
        mostrar_procesos();
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
                    Fila[5] = (int) (Math.random() * 50) + " %"; // CPU %
                    Fila[6] = Math.round(Math.random() * 10 * 100.0) / 100.0 + " MB/s"; // Disco
                    
                    int pid = -1;
                    if (sep.length > 1) {
                        try {
                            pid = Integer.parseInt(sep[1]);
                        } catch (NumberFormatException e) {
                            pid = -1; // Valor inválido
                        }
                    }
                    
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
        } catch (Exception err) {
            err.printStackTrace();
        }

    }
    //Anderson Rodriguez
    private void iniciarActualizacionMemoria() {
    Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                Map<Integer, String> memoriaPorPID = new HashMap<>();

                int i = 0;
                while ((line = input.readLine()) != null) {
                    if (i >= 4) {
                        String[] sep = line.trim().split("\\s+");
                        if (sep.length >= 6) {
                            try {
                                int pid = Integer.parseInt(sep[1]);
                                String mem = sep[4] + " " + sep[5]; // Ejemplo: "12,345 KB"
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

                // Actualizar la tabla
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

    
    //Anderson Rodriguez
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

                if (column == 4) { // Solo aplicar estilo si es la columna de uso de memoria
                    String usoMemoriaStr = value.toString().replace("MB", "").replace(",", "").trim();
                
                     //System.out.println("Memoria: " + usoMemoriaStr);

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
                } else {
                    // Para otras columnas, mantener estilo normal
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        };
    }
    //Anderson Rodriguez
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
    
    //Cristofer
    // procedimiento de limpieza de la tabla la restablece de a los parametros inisciales
  void LimpiarTabla() {
    jtabla_datos.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "Nombre", "PID", "Tipo de sesión", "Número de sesión", 
                "Uso de memoria", "CPU (%)", "Disco (MB/s)", "Red (%)"
            }
    ) {
        boolean[] canEdit = new boolean[]{
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }
    });
}

    

    
    //Ruddyard Castro 9959-23-1409
      public void Matar_proceso() {
        modelo = (DefaultTableModel) jtabla_datos.getModel();
        String StrCelda = String.valueOf(modelo.getValueAt(jtabla_datos.getSelectedRow(), 0));
        if (StrCelda == "") {
            JOptionPane.showMessageDialog(null, "ERROR, No se ha selecionado ningun proceso", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                Process hijo;
                hijo = Runtime.getRuntime().exec("taskkill /F /IM " + StrCelda);
                hijo.waitFor();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                    
}
private Map<String, String> obtenerInformacionSistema() {
    Map<String, String> info = new HashMap<>();

    try {
        // Device name
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
                        .addComponent(btnBuscar))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(No_procesos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jIniciar_procesos)
                    .addComponent(btnSuspenderProceso)
                    .addComponent(btnEspesifiaciones))
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOrdenar)
                    .addComponent(btnBuscar))
                .addContainerGap())
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
    private javax.swing.JButton btnOrdenar;
    private javax.swing.JButton btnSuspenderProceso;
    private javax.swing.JButton jIniciar_procesos;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtabla_datos;
    // End of variables declaration//GEN-END:variables
}
