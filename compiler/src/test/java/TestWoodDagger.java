import com.danielpuiu.wooddagger.annotations.Getter;
import com.danielpuiu.wooddagger.annotations.Setter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created: 20/04/2017
 * Author:  dpuiu
 */
public class TestWoodDagger {

    final private static String STRING_VALUE = "VALUE";
    final private static int INT_VALUE = 100;

    @Test
    public void testGetterWithDefaultName() {
        DefaultNameGetterStringProperty objectGetter = new DefaultNameGetterStringProperty();

        assertEquals(null, objectGetter.getProperty());
        objectGetter.property = STRING_VALUE;
        assertEquals(STRING_VALUE, objectGetter.getProperty());

        DefaultNameGetterIntProperty primitiveGetter = new DefaultNameGetterIntProperty();

        assertEquals(0, primitiveGetter.getProperty());
        primitiveGetter.property = INT_VALUE;
        assertEquals(INT_VALUE, primitiveGetter.getProperty());
    }

    @Test
    public void testGetterWithCustomName() {
        CustomNameGetterStringProperty objectGetter = new CustomNameGetterStringProperty();

        assertEquals(null, objectGetter.getMyProperty());
        objectGetter.property = STRING_VALUE;
        assertEquals(STRING_VALUE, objectGetter.getMyProperty());

        CustomNameGetterIntProperty primitiveGetter = new CustomNameGetterIntProperty();

        assertEquals(0, primitiveGetter.getMyProperty());
        primitiveGetter.property = INT_VALUE;
        assertEquals(INT_VALUE, primitiveGetter.getMyProperty());
    }

    @Test
    public void testSetterWithDefaultName() {
        DefaultNameSetterStringProperty objectSetter = new DefaultNameSetterStringProperty();

        assertEquals(null, objectSetter.property);
        objectSetter.setProperty(STRING_VALUE);
        assertEquals(STRING_VALUE, objectSetter.property);

        DefaultNameSetterIntProperty primitiveSetter = new DefaultNameSetterIntProperty();

        assertEquals(0, primitiveSetter.property);
        primitiveSetter.setProperty(INT_VALUE);
        assertEquals(INT_VALUE, primitiveSetter.property);
    }

    @Test
    public void testSetterWithCustomName() {
        CustomNameSetterStringProperty objectSetter = new CustomNameSetterStringProperty();

        assertEquals(null, objectSetter.property);
        objectSetter.setMyProperty(STRING_VALUE);
        assertEquals(STRING_VALUE, objectSetter.property);

        CustomNameSetterIntProperty primitiveSetter = new CustomNameSetterIntProperty();

        assertEquals(0, primitiveSetter.property);
        primitiveSetter.setMyProperty(INT_VALUE);
        assertEquals(INT_VALUE, primitiveSetter.property);
    }

    @Test
    public void testStaticGetter() {
        assertEquals(0, StaticGetter.getValue());
    }
}

class StaticGetter {
    @Getter
    static int value;
}

class DefaultNameGetterIntProperty {
    @Getter
    int property;
}

class CustomNameGetterIntProperty {
    @Getter(name="getMyProperty")
    int property;
}

class DefaultNameGetterStringProperty {
    @Getter
    String property;
}

class CustomNameGetterStringProperty {
    @Getter(name="getMyProperty")
    String property;
}

class DefaultNameSetterIntProperty {
    @Setter
    int property;
}

class CustomNameSetterIntProperty {
    @Setter(name="setMyProperty")
    int property;
}

class DefaultNameSetterStringProperty {
    @Setter
    String property;
}

class CustomNameSetterStringProperty {
    @Setter(name="setMyProperty")
    String property;
}

