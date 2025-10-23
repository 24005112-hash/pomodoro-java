import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pomodoro simple usando Swing.
 * - Duración por defecto: 25 min trabajo / 5 min descanso
 * - Botones: Iniciar/Pausar, Reiniciar, Configurar tiempos
 *
 * Compilar:
 * javac Pomodoro.java
 * Ejecutar:
 * java Pomodoro
 */
public class Pomodoro extends JFrame {
    private static final int DEFAULT_WORK_MIN = 25;
    private static final int DEFAULT_BREAK_MIN = 5;

    private int workSeconds = DEFAULT_WORK_MIN * 60;
    private int breakSeconds = DEFAULT_BREAK_MIN * 60;

    private boolean isWorkPeriod = true;
    private boolean running = false;

    private int remainingSeconds;

    private JLabel lblTitle;
    private JLabel lblTimer;
    private JButton btnStartPause;
    private JButton btnReset;
    private JButton btnSwitchMode;
    private Timer swingTimer;

    public Pomodoro() {
        super("Pomodoro Simple");
        initUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(320, 200);
        setLocationRelativeTo(null);
        resetTimerState();
    }

    private void initUI() {
        lblTitle = new JLabel("", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTimer = new JLabel("00:00", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Monospaced", Font.PLAIN, 36));

        btnStartPause = new JButton("Iniciar");
        btnReset = new JButton("Reiniciar");
        btnSwitchMode = new JButton("Configurar tiempos");

        JPanel center = new JPanel(new BorderLayout());
        center.add(lblTitle, BorderLayout.NORTH);
        center.add(lblTimer, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(btnStartPause);
        bottom.add(btnReset);
        bottom.add(btnSwitchMode);

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Timer: actualiza cada 1 segundo
        swingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });

        // Botón Iniciar/Pausar
        btnStartPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleStartPause();
            }
        });

        // Botón Reiniciar
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = false;
                swingTimer.stop();
                resetTimerState();
                updateUIFromState();
            }
        });

        // Botón Configurar tiempos
        btnSwitchMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openConfigDialog();
            }
        });

        updateUIFromState();
    }

    private void openConfigDialog() {
        JTextField tfWork = new JTextField(String.valueOf(DEFAULT_WORK_MIN));
        JTextField tfBreak = new JTextField(String.valueOf(DEFAULT_BREAK_MIN));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Minutos de trabajo:"));
        panel.add(tfWork);
        panel.add(new JLabel("Minutos de descanso:"));
        panel.add(tfBreak);

        int result = JOptionPane.showConfirmDialog(this, panel, "Configurar Pomodoro",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int w = Integer.parseInt(tfWork.getText().trim());
                int b = Integer.parseInt(tfBreak.getText().trim());
                if (w <= 0 || b <= 0) throw new NumberFormatException();
                workSeconds = w * 60;
                breakSeconds = b * 60;
                resetTimerState();
                updateUIFromState();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduce valores numéricos válidos (>0).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetTimerState() {
        isWorkPeriod = true;
        remainingSeconds = workSeconds;
        running = false;
    }

    private void toggleStartPause() {
        if (running) {
            running = false;
            swingTimer.stop();
            btnStartPause.setText("Reanudar");
        } else {
            running = true;
            swingTimer.start();
            btnStartPause.setText("Pausar");
        }
        updateUIFromState();
    }

    private void tick() {
        if (!running) return;
        if (remainingSeconds > 0) {
            remainingSeconds--;
            updateTimerLabel();
        } else {
            Toolkit.getDefaultToolkit().beep();
            // Cambiar de periodo
            isWorkPeriod = !isWorkPeriod;
            remainingSeconds = isWorkPeriod ? workSeconds : breakSeconds;
            String periodo = isWorkPeriod ? "Trabajo" : "Descanso";
            JOptionPane.showMessageDialog(this, "¡Cambio a periodo: " + periodo + "!", "Periodo terminado", JOptionPane.INFORMATION_MESSAGE);
            updateUIFromState();
        }
    }

    private void updateUIFromState() {
        lblTitle.setText(isWorkPeriod ? "Tiempo de trabajo" : "Tiempo de descanso");
        updateTimerLabel();
        btnStartPause.setText(running ? "Pausar" : (remainingSeconds == (isWorkPeriod ? workSeconds : breakSeconds) ? "Iniciar" : "Reanudar"));
    }

    private void updateTimerLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        lblTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public static void main(String[] args) {
        // Ejecuta GUI en el hilo de eventos
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Pomodoro app = new Pomodoro();
                app.setVisible(true);
            }
        });
    }
}
