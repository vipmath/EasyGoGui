//----------------------------------------------------------------------------
// $Id$
// $Source$
//----------------------------------------------------------------------------

package gtp;

import utils.ErrorMessage;

//----------------------------------------------------------------------------

/** Exception indication the failure of a GTP command. */
public class GtpError
    extends ErrorMessage
{
    public GtpError(String s)
    {
        super(s);
    }
}    

//----------------------------------------------------------------------------
