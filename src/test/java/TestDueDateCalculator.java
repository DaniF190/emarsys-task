import com.emarsys.calculator.DueDateCalculator;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TestDueDateCalculator {

    @Test
    void testCorrectInput () {

        final var CORRECT_INPUT_FILENAME = "test1.txt";
        final var TURNAROUND_TIME = 160;

        var inputStream = Objects.requireNonNull(this.getClass()
                .getClassLoader()
                .getResourceAsStream(CORRECT_INPUT_FILENAME));

        var lines = new ArrayList<String>();

        try (Scanner scanner = new Scanner(inputStream)) {

            while (scanner.hasNext()) {

                lines.add(scanner.nextLine());
            }
        }

        for (String line : lines) {

            var submitDate = new Date();

            try {
                submitDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(line);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date finalSubmitDate = submitDate;

            assertDoesNotThrow(() -> DueDateCalculator.calculateDueDate(finalSubmitDate, TURNAROUND_TIME));
        }
    }
}
