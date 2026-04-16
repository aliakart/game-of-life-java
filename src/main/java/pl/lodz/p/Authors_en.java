package pl.lodz.p;

import java.util.ListResourceBundle;

public class Authors_en extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"Authors", new String[] {"Artsiom Aliaksandrau"}}
        };
    }
}
