/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * JFlex 1.4.1                                                             *
 * Copyright (C) 1998-2004  Gerwin Klein <lsf@jflex.de>                    *
 * All rights reserved.                                                    *
 *                                                                         *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License. See the file      *
 * COPYRIGHT for more information.                                         *
 *                                                                         *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with this program; if not, write to the Free Software Foundation, Inc., *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package weka.core.parser.JFlex;



/**
 * This Exception is used in class CharClasses.
 *
 * @author Gerwin Klein
 * @version JFlex 1.4.1, $Revision: 1.1 $, $Date: 2008-05-09 09:14:11 $
 */
public class CharClassException extends RuntimeException {

  /**
   * Creates a new CharClassException without message
   */
  public CharClassException() {
  }


  /**
   * Creates a new CharClassException with the specified message
   *
   * @param message   the error description presented to the user.
   */
  public CharClassException(String message) {
    super(message);
  }

}
