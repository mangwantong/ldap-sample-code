/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 */
package samplecode;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;

/**
 * thrown when the password modify extended operation failed.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 28, 2011")
@CodeVersion("1.0")
@SuppressWarnings("serial")
public class PasswordModifyExtendedOperationFailedException extends Exception {

    private final ResultCode resultCode;

    /**
     * retrieves the result code associated with the exception.
     * 
     * @return result code from the exception.
     */
    public ResultCode getResultCode() {
	return this.resultCode;
    }

    /**
     * Constructs the object and sets its state with the {@code resultCode}
     * parameter.
     * 
     * @param resultCode
     *            The result code from a failed password modify extended
     *            operation failed exception.
     */
    public PasswordModifyExtendedOperationFailedException(
	    final ResultCode resultCode) {
	Validator.ensureNotNull(resultCode);
	this.resultCode = resultCode;
    }

}
