package samplecode.display;


import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.controls.*;
import samplecode.annotation.CodeVersion;


/**
 * Provides methods used to print {@code Control}
 * information in a human-readable format.
 *
 * @author Terry Gardner
 */
@CodeVersion("1.1")
public class ControlDisplayValues
{

   private final Control control;


   public ControlDisplayValues(final Control control)
   {
      this.control = control;
   }


   @Override
   public String toString()
   {
      return "ControlDisplayValues [" + (control != null ?
         "control=" + control : "") + "]";
   }


   /**
    * Display the control in a generic fashion.
    *
    * @return A string representation of the value of the control.
    */
   public Object displayControl()
   {
      final StringBuilder builder = new StringBuilder(control.getClass().getCanonicalName());
      builder.append(" ");
      builder.append(control.getControlName());
      builder.append(" ");
      builder.append(control.getOID());
      builder.append(" ");
      if(control instanceof PasswordExpiredControl)
      {
         control.toString(builder);
      }
      else if(control instanceof PasswordExpiringControl)
      {
         control.toString(builder);
      }
      return builder.toString();
   }

}
