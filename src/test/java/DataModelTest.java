import com.github.rschmitt.dynamicobject.DynamicObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;



public class DataModelTest {
    private DataModel dataModel;

    private static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
                e.printStackTrace(); // replace with more robust error handling
                return TERMINATE;
            }

            @Override public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if(e!=null)return handleException(e);
                Files.delete(dir);
                return CONTINUE;
            }
        });
    }

    @BeforeClass
    public static void setupDataModel() {
        DynamicObject.newInstance(DataModel.class)
                .fromFile("breast-cancer-wisconsin.data.txt")
                .save("testFile");
    }

    @Before
    public void setup() {
        dataModel = DynamicObject.newInstance(DataModel.class)
                .loadFromEdn("testFile");
    }

    @AfterClass
    public static void cleanUp() {
        try {
            deleteFileOrFolder(Paths.get(new File("/edn/").getAbsolutePath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetData() {
        assertThat(dataModel.getData(), is(notNullValue()));
    }
}
