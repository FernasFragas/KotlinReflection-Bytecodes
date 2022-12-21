package pt.isel.Generated;

import java.lang.Object;
import java.lang.Override;
import pt.isel.JsonTokens;
import pt.isel.Setter;

public class SetterStudent_birth implements Setter {
  @Override
  public void apply(Object target, JsonTokens tokens) {
    java.lang.String v = (java.lang.String) pt.isel.JsonParserDynamic.INSTANCE.parse(tokens, kotlin.jvm.JvmClassMappingKt.getKotlinClass(java.lang.String.class));
    if (v!=null) { 
         pt.isel.sample.Date newv = pt.isel.JsonToDate.INSTANCE.converter(v); ((pt.isel.sample.Student) target).setBirth(newv); 
        };
  }
}
