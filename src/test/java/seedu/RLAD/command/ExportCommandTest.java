package seedu.RLAD.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportCommandTest {

    @TempDir
    Path tempDir;

    private TransactionManager tm;
    private Ui ui;

    @BeforeEach
    void setUp() {
        tm = new TransactionManager();
        System.setIn(new ByteArrayInputStream("".getBytes()));
        ui = new Ui();
    }

    @Test
    void execute_withTransactions_createsFile() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Lunch"));
        tm.addTransaction(new Transaction("debit", "transport", 3.50,
                LocalDate.of(2026, 3, 16), "Bus"));

        Path target = tempDir.resolve("out.csv");
        ExportCommand cmd = new ExportCommand(target.toString());
        cmd.execute(tm, ui);

        assertTrue(Files.exists(target));
        List<String> lines = Files.readAllLines(target);
        assertEquals(3, lines.size());
        assertEquals("HashID,Type,Category,Amount,Date,Description", lines.get(0));
    }

    @Test
    void execute_emptyTransactions_noFileCreated() throws RLADException {
        Path target = tempDir.resolve("out.csv");
        ExportCommand cmd = new ExportCommand(target.toString());
        cmd.execute(tm, ui);
        assertTrue(!Files.exists(target));
    }

    @Test
    void execute_invalidPath_throwsException() {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));
        ExportCommand cmd = new ExportCommand("/nonexistent/dir/xyz/out.csv");
        assertThrows(RLADException.class, () -> cmd.execute(tm, ui));
    }

    @Test
    void execute_specialCharsInDescription_exportedCorrectly() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 25.00,
                LocalDate.of(2026, 1, 1), "Meal, \"fancy\" place"));

        Path target = tempDir.resolve("special.csv");
        ExportCommand cmd = new ExportCommand(target.toString());
        cmd.execute(tm, ui);

        List<String> lines = Files.readAllLines(target);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("\"Meal, \"\"fancy\"\" place\""));
    }

    @Test
    void hasValidArgs_always_returnsTrue() {
        assertEquals(true, new ExportCommand("").hasValidArgs());
        assertEquals(true, new ExportCommand("test.csv").hasValidArgs());
    }

    @Test
    void execute_absoluteFilename_createsFileAtPath() throws Exception {
        tm.addTransaction(new Transaction("credit", "food", 50.00,
                LocalDate.of(2026, 3, 15), "Test"));

        Path target = tempDir.resolve("absolute.csv").toAbsolutePath();
        ExportCommand cmd = new ExportCommand(target.toString());
        cmd.execute(tm, ui);

        assertTrue(Files.exists(target));
    }
}
