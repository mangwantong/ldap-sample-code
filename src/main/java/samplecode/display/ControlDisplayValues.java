package samplecode.display;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.controls.PasswordExpiredControl;
import com.unboundid.ldap.sdk.controls.PasswordExpiringControl;
import com.unboundid.util.Validator;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;


@Author("terry.gardner@unboundid.com")
@CodeVersion("1.0")
/**
 * Provides methods used to print {@code Control}
 * information in a human-readable format.
 */
public class ControlDisplayValues {

  public ControlDisplayValues(final Control control) {
    Validator.ensureNotNull(control);
    this.control = control;
  }



  @Override
  public String toString() {
    return "ControlDisplayValues [" + (control != null ?
      "control=" + control : "") + "]";
  }



  /**
   * Display the control in a generic fashion.
   *
   * @return A string representation of the value of the control.
   */
  public Object msg() {
    final StringBuilder builder = new StringBuilder(control
      .getClass().getCanonicalName());
    builder.append(" ");
    builder.append(control.getControlName());
    builder.append(" ");
    builder.append(control.getOID());
    builder.append(" ");
    if(control instanceof PasswordExpiredControl) {
      control.toString(builder);
    } else if(control instanceof PasswordExpiringControl) {
      control.toString(builder);
    }
    return builder.toString();
  }



  private final Control control;

}
