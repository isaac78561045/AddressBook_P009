package fisei.uta.edu.ec.addressbook;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Prueba instrumentada, que se ejecutará en un dispositivo Android.
 *
 * @see <a href="http://d.android.com/tools/testing">Documentación de pruebas</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Contexto de la aplicación bajo prueba.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("fisei.uta.edu.ec.addressbook", appContext.getPackageName());
    }
}
