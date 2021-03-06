/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * FindWithCapabilities.java
 * Copyright (C) 2006 University of Waikato, Hamilton, New Zealand
 */

package weka.core;

import weka.core.Capabilities.Capability;
import weka.gui.GenericPropertiesCreator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Locates all classes with certain capabilities. <p/>
 * 
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -num-instances &lt;num&gt;
 *  The minimum number of instances (default 1).</pre>
 * 
 * <pre> -nominal-class
 *  Must handle nominal classes.</pre>
 * 
 * <pre> -binary-class
 *  Must handle binary classes.</pre>
 * 
 * <pre> -numeric-class
 *  Must handle numeric classes.</pre>
 * 
 * <pre> -string-class
 *  Must handle string classes.</pre>
 * 
 * <pre> -date-class
 *  Must handle date classes.</pre>
 * 
 * <pre> -relational-class
 *  Must handle relational classes.</pre>
 * 
 * <pre> -no-class
 *  Doesn't need a class.</pre>
 * 
 * <pre> -nominal-atts
 *  Must handle nominal attributes.</pre>
 * 
 * <pre> -numeric-atts
 *  Must handle numeric attributes.</pre>
 * 
 * <pre> -string-atts
 *  Must handle string attributes.</pre>
 * 
 * <pre> -date-atts
 *  Must handle date attributes.</pre>
 * 
 * <pre> -relational-atts
 *  Must handle relational attributes.</pre>
 * 
 * <pre> -only-multiinstance
 *  Must handle multi-instance data.</pre>
 * 
 * <pre> -W &lt;classname&gt;
 *  The Capabilities handler to base the handling on.
 *  The other parameters can be used to override the ones
 *  determined from the handler. Additional parameters for
 *  handler can be passed on after the '--'.
 *  Either '-W' or '-t' can be used.</pre>
 * 
 * <pre> -t &lt;file&gt;
 *  The dataset to base the capabilities on.
 *  The other parameters can be used to override the ones
 *  determined from the handler.
 *  Either '-t' or '-W' can be used.</pre>
 * 
 * <pre> -c &lt;num&gt;
 *  The index of the class attribute, -1 for none.
 *  'first' and 'last' are also valid.
 *  Only in conjunction with option '-t'.</pre>
 * 
 * <pre> -superclass
 *  Superclass to look for in the packages.
 * </pre>
 * 
 * <pre> -packages
 *  Comma-separated list of packages to search in.</pre>
 * 
 * <pre> -generic
 *  Retrieves the package list from the GenericPropertiesCreator
 *  for the given superclass. (overrides -packages &lt;list&gt;).</pre>
 * 
 * <pre> -misses
 *  Also prints the classname that didn't match the criteria.</pre>
 * 
 <!-- options-end -->
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.2 $
 * @see Capabilities
 * @see Capabilities.Capability
 * @see GenericPropertiesCreator
 */
public class FindWithCapabilities 
  implements OptionHandler, CapabilitiesHandler {

  /** the capabilities to look for */
  protected Capabilities m_Capabilities = new Capabilities(this);

  /** the packages to search in */
  protected Vector m_Packages = new Vector();

  /** a capabilities handler to retrieve the capabilities from */
  protected CapabilitiesHandler m_Handler = null;

  /** a file the capabilities can be based on */
  protected String m_Filename = "";
  
  /** the class index, in case the capabilities are based on a file */
  protected SingleIndex m_ClassIndex = new SingleIndex();
  
  /** the superclass from the GenericPropertiesCreator to retrieve the packages from */
  protected String m_Superclass = "";
  
  /** whether to use the GenericPropertiesCreator with the superclass */
  protected boolean m_GenericPropertiesCreator = false;
  
  /** the classes that matched */
  protected Vector m_Matches = new Vector();
  
  /** the class that didn't match */
  protected Vector m_Misses = new Vector();
  
  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();
    
    result.addElement(new Option(
        "\tThe minimum number of instances (default 1).",
        "num-instances", 1, "-num-instances <num>"));
    
    result.addElement(new Option(
        "\tMust handle nominal classes.",
        "nominal-class", 0, "-nominal-class"));
    
    result.addElement(new Option(
        "\tMust handle binary classes.",
        "binary-class", 0, "-binary-class"));
    
    result.addElement(new Option(
        "\tMust handle numeric classes.",
        "numeric-class", 0, "-numeric-class"));
    
    result.addElement(new Option(
        "\tMust handle string classes.",
        "string-class", 0, "-string-class"));
    
    result.addElement(new Option(
        "\tMust handle date classes.",
        "date-class", 0, "-date-class"));
    
    result.addElement(new Option(
        "\tMust handle relational classes.",
        "relational-class", 0, "-relational-class"));
    
    result.addElement(new Option(
        "\tDoesn't need a class.",
        "no-class", 0, "-no-class"));

    result.addElement(new Option(
        "\tMust handle nominal attributes.",
        "nominal-atts", 0, "-nominal-atts"));
    
    result.addElement(new Option(
        "\tMust handle numeric attributes.",
        "numeric-atts", 0, "-numeric-atts"));
    
    result.addElement(new Option(
        "\tMust handle string attributes.",
        "string-atts", 0, "-string-atts"));
    
    result.addElement(new Option(
        "\tMust handle date attributes.",
        "date-atts", 0, "-date-atts"));
    
    result.addElement(new Option(
        "\tMust handle relational attributes.",
        "relational-atts", 0, "-relational-atts"));
    
    result.addElement(new Option(
        "\tMust handle multi-instance data.",
        "only-multiinstance", 0, "-only-multiinstance"));

    result.addElement(new Option(
        "\tThe Capabilities handler to base the handling on.\n"
	+ "\tThe other parameters can be used to override the ones\n"
	+ "\tdetermined from the handler. Additional parameters for\n"
	+ "\thandler can be passed on after the '--'.\n"
	+ "\tEither '-W' or '-t' can be used.",
        "W", 1, "-W <classname>"));
    
    result.addElement(new Option(
        "\tThe dataset to base the capabilities on.\n"
	+ "\tThe other parameters can be used to override the ones\n"
	+ "\tdetermined from the handler.\n"
	+ "\tEither '-t' or '-W' can be used.",
        "t", 1, "-t <file>"));
    
    result.addElement(new Option(
        "\tThe index of the class attribute, -1 for none.\n"
	+ "\t'first' and 'last' are also valid.\n"
	+ "\tOnly in conjunction with option '-t'.",
        "c", 1, "-c <num>"));
    
    result.addElement(new Option(
        "\tSuperclass to look for in the packages.\n",
        "superclass", 1, "-superclass"));

    result.addElement(new Option(
        "\tComma-separated list of packages to search in.",
        "packages", 1, "-packages"));

    result.addElement(new Option(
	  "\tRetrieves the package list from the GenericPropertiesCreator\n"
	+ "\tfor the given superclass. (overrides -packages <list>).",
        "generic", 1, "-generic"));

    result.addElement(new Option(
        "\tAlso prints the classname that didn't match the criteria.",
        "misses", 0, "-misses"));
    
    return result.elements();
  }
  
  /**
   * Parses a given list of options. 
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String      		tmpStr;
    Class			cls;
    CapabilitiesHandler		handler;
    boolean			initialized;
    StringTokenizer		tok;
    GenericPropertiesCreator	creator;
    Properties			props;

    m_Capabilities = new Capabilities(this);
    initialized    = false;
    
    tmpStr = Utils.getOption('W', options);
    if (tmpStr.length() != 0) {
      cls = Class.forName(tmpStr);
      if (ClassDiscovery.hasInterface(CapabilitiesHandler.class, cls)) {
	initialized = true;
	handler = (CapabilitiesHandler) cls.newInstance();
	if (handler instanceof OptionHandler)
	  ((OptionHandler) handler).setOptions(Utils.partitionOptions(options));
	setHandler(handler);
      }
      else {
	throw new IllegalArgumentException("Class '" + tmpStr + "' is not a CapabilitiesHandler!");
      }
    }
    else {
      tmpStr = Utils.getOption('c', options);
      if (tmpStr.length() != 0)
	setClassIndex(tmpStr);
      else
	setClassIndex("last");

      tmpStr = Utils.getOption('t', options);
      setFilename(tmpStr);
    }

    tmpStr = Utils.getOption("num-instances", options);
    if (tmpStr.length() != 0)
      m_Capabilities.setMinimumNumberInstances(Integer.parseInt(tmpStr));
    else if (!initialized)
      m_Capabilities.setMinimumNumberInstances(1);
    
    if (Utils.getFlag("no-class", options))
      enable(Capability.NO_CLASS);
    
    if (!m_Capabilities.handles(Capability.NO_CLASS)) {
      if (Utils.getFlag("nominal-class", options))
	 enable(Capability.NOMINAL_CLASS);
      if (Utils.getFlag("binary-class", options))
	 enable(Capability.BINARY_CLASS);
      if (Utils.getFlag("numeric-class", options))
	 enable(Capability.NUMERIC_CLASS);
      if (Utils.getFlag("string-class", options))
	 enable(Capability.STRING_CLASS);
      if (Utils.getFlag("date-class", options))
	 enable(Capability.DATE_CLASS);
      if (Utils.getFlag("relational-class", options))
	 enable(Capability.RELATIONAL_CLASS);
    }
    
    if (Utils.getFlag("nominal-atts", options))
      enable(Capability.NOMINAL_ATTRIBUTES);
    if (Utils.getFlag("binary-atts", options))
      enable(Capability.BINARY_ATTRIBUTES);
    if (Utils.getFlag("numeric-atts", options))
      enable(Capability.NUMERIC_ATTRIBUTES);
    if (Utils.getFlag("string-atts", options))
      enable(Capability.STRING_ATTRIBUTES);
    if (Utils.getFlag("date-atts", options))
      enable(Capability.DATE_ATTRIBUTES);
    if (Utils.getFlag("relational-atts", options))
      enable(Capability.RELATIONAL_ATTRIBUTES);
    
    if (Utils.getFlag("only-multiinstance", options))
      enable(Capability.ONLY_MULTIINSTANCE);
    
    tmpStr = Utils.getOption("superclass", options);
    if (tmpStr.length() != 0)
      m_Superclass = tmpStr;
    else
      throw new IllegalArgumentException("A superclass has to be specified!");
    
    tmpStr = Utils.getOption("packages", options);
    if (tmpStr.length() != 0) {
      tok        = new StringTokenizer(tmpStr, ",");
      m_Packages = new Vector();
      while (tok.hasMoreTokens())
	m_Packages.add(tok.nextToken());
    }
    
    if (Utils.getFlag("generic", options)) {
      creator    = new GenericPropertiesCreator();
      creator.execute(false);
      props	 = creator.getInputProperties();
      tok        = new StringTokenizer(props.getProperty(m_Superclass), ",");
      m_Packages = new Vector();
      while (tok.hasMoreTokens())
	m_Packages.add(tok.nextToken());
    }
  }
  
  /**
   * Gets the current settings of this object.
   * 
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector 	result;
    String[]	options;
    int		i;

    result = new Vector();
    
    result.add("-num-instances");
    result.add("" + m_Capabilities.getMinimumNumberInstances());
    
    if (m_Capabilities.handles(Capability.NO_CLASS)) {
      result.add("-no-class");
    }
    else {
      if (m_Capabilities.handles(Capability.NOMINAL_CLASS))
	result.add("-nominal-class");
      if (m_Capabilities.handles(Capability.BINARY_CLASS))
	result.add("-binary-class");
      if (m_Capabilities.handles(Capability.NUMERIC_CLASS))
	result.add("-numeric-class");
      if (m_Capabilities.handles(Capability.STRING_CLASS))
	result.add("-string-class");
      if (m_Capabilities.handles(Capability.DATE_CLASS))
	result.add("-date-class");
      if (m_Capabilities.handles(Capability.RELATIONAL_CLASS))
	result.add("-relational-class");
    }
    
    if (m_Capabilities.handles(Capability.NOMINAL_ATTRIBUTES))
	result.add("-nominal-atts");
    if (m_Capabilities.handles(Capability.BINARY_ATTRIBUTES))
	result.add("-binary-atts");
    if (m_Capabilities.handles(Capability.NUMERIC_ATTRIBUTES))
	result.add("-numeric-atts");
    if (m_Capabilities.handles(Capability.STRING_ATTRIBUTES))
	result.add("-string-atts");
    if (m_Capabilities.handles(Capability.DATE_ATTRIBUTES))
	result.add("-date-atts");
    if (m_Capabilities.handles(Capability.RELATIONAL_ATTRIBUTES))
	result.add("-relational-atts");

    if (m_Capabilities.handles(Capability.ONLY_MULTIINSTANCE))
      result.add("-only-multiinstance");

    if (getHandler() != null) {
      result.add("-W");
      result.add(getHandler().getClass().getName());
      if (getHandler() instanceof OptionHandler) {
	result.add("--");
	options = ((OptionHandler) getHandler()).getOptions();
	for (i = 0; i < options.length; i++)
	  result.add(options[i]);
      }
    }
    else if (getFilename().length() != 0) {
      result.add("-t");
      result.add(getFilename());
      result.add("-c");
      result.add(m_ClassIndex.getSingleIndex());
    }

    if (m_Superclass.length() != 0) {
      result.add("-superclass");
      result.add(m_Superclass);
    }
    else {
      result.add("-packages");
      result.add(m_Packages.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
    }
    
    return (String[]) result.toArray(new String[result.size()]);
  }
  
  /**
   * sets the Capabilities handler to generate the data for
   */
  public void setHandler(CapabilitiesHandler value) {
    m_Handler = value;
    setCapabilities(m_Handler.getCapabilities());
  }
  
  /**
   * returns the current set CapabilitiesHandler to generate the dataset
   * for, can be null
   */
  public CapabilitiesHandler getHandler() {
    return m_Handler;
  }
  
  /**
   * Sets the dataset filename to base the capabilities on. It immediately
   * loads the dataset and retrieves the capabilities from it.
   */
  public void setFilename(String value) {
    Instances		insts;
    
    m_Filename = value;

    if (m_Filename.length() != 0) {
      try {
        insts  = new Instances(new BufferedReader(new FileReader(m_Filename)));
        m_ClassIndex.setUpper(insts.numAttributes());
        insts.setClassIndex(Integer.parseInt(getClassIndex()) - 1);
        
        setCapabilities(Capabilities.forInstances(insts));
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }
  }
  
  /**
   * returns the current filename for the dataset to base the capabilities on
   */
  public String getFilename() {
    return m_Filename;
  }
  
  /**
   * sets the class index, -1 for none, first and last are also valid
   */
  public void setClassIndex(String value) {
    if (value.equals("-1"))
      m_ClassIndex = null;
    else
      m_ClassIndex = new SingleIndex(value);
  }
  
  /**
   * returns the current current class index, -1 if no class attribute
   */
  public String getClassIndex() {
    if (m_ClassIndex == null)
      return "-1";
    else
      return "" + m_ClassIndex.getIndex();
  }

  /**
   * enables the given capability
   * 
   * @param c		the capability to enable
   */
  public void enable(Capability c) {
    m_Capabilities.enable(c);
  }

  /**
   * disables the given capability
   * 
   * @param c		the capability to disable
   */
  public void disable(Capability c) {
    m_Capabilities.disable(c);
  }

  /**
   * returns true if the given capability can be handled
   * 
   * @param c		the capability to check
   * @return		true if the capability can be handled
   */
  public boolean handles(Capability c) {
    return m_Capabilities.handles(c);
  }
  
  /**
   * The capabilities to search for.
   *
   * @return            the capabilities to search for
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    return m_Capabilities;
  }
  
  /**
   * Uses the given Capabilities for the search.
   * 
   * @param c		the capabilities to use for the search
   */
  public void setCapabilities(Capabilities c) {
    m_Capabilities = (Capabilities) c.clone();
  }

  /**
   * returns the matches from the last find call
   * 
   * @return		the matching classname from the last find run
   */
  public Vector getMatches() {
    return m_Matches;
  }

  /**
   * returns the misses from the last find call
   * 
   * @return		the classnames that didn't match from the last find run
   */
  public Vector getMisses() {
    return m_Misses;
  }
  
  /**
   * returns a list with all the classnames that fit the criteria
   * 
   * @return		contains all classnames that fit the criteria
   */
  public Vector find() {
    Vector		list;
    int			i;
    Class		cls;
    Object		obj;
    CapabilitiesHandler	handler;
    boolean		fits;
    Capabilities	caps;
    
    m_Matches = new Vector();
    m_Misses  = new Vector();

    list = ClassDiscovery.find(m_Superclass, (String[]) m_Packages.toArray(new String[m_Packages.size()]));
    for (i = 0; i < list.size(); i++) {
      try {
	cls = Class.forName((String) list.get(i));
	obj = cls.newInstance();
	
	// exclude itself
	if (cls == this.getClass())
	  continue;
	
	// really a CapabilitiesHandler?
	if (!(obj instanceof CapabilitiesHandler))
	  continue;

	// check capabilities enumeration
	handler = (CapabilitiesHandler) obj;
	caps    = handler.getCapabilities();
	fits    = true;
	for (Capability cap: Capability.values()) {
	  if (m_Capabilities.handles(cap)) {
	    if (!(caps.handles(cap))) {
	      fits = false;
	      break;
	    }
	  }
	}
	if (!fits) {
	  m_Misses.add(list.get(i));
	  continue;
	}
	
	// other stuff
	if (caps.getMinimumNumberInstances() > m_Capabilities.getMinimumNumberInstances()) {
	  m_Misses.add(list.get(i));
	  continue;
	}
	
	// matches all criteria!
	m_Matches.add(list.get(i));
      }
      catch (Exception e) {
	// ignore
      }
    }
    
    return m_Matches;
  }

  /**
   * Executes the location of classes with parameters from the commandline.
   * 
   * @param args	the commandline parameters
   */
  public static void main(String[] args) {
    FindWithCapabilities 	find;
    Vector 			list;
    String			result;
    int				i;
    boolean			printMisses;
    
    printMisses = false;
    
    try {
      find = new FindWithCapabilities();
      
      try {
	printMisses = Utils.getFlag("misses", args);
        find.setOptions(args);
        Utils.checkForRemainingOptions(args);
      } 
      catch (Exception ex) {
        result = ex.getMessage() + "\n\n" + find.getClass().getName().replaceAll(".*\\.", "") + " Options:\n\n";
        Enumeration enm = find.listOptions();
        while (enm.hasMoreElements()) {
          Option option = (Option) enm.nextElement();
          result += option.synopsis() + "\n" + option.description() + "\n";
        }
        throw new Exception(result);
      }
      
      System.out.println("\nSearching for the following Capabilities:\n\n" + find.getCapabilities());
      
      find.find();

      // matches
      list = find.getMatches();
      if (list.size() == 1)
	System.out.println("\nFound " + list.size() + " class that matched the criteria:\n");
      else
	System.out.println("\nFound " + list.size() + " classes that matched the criteria:\n");
      for (i = 0; i < list.size(); i++)
	System.out.println(list.get(i));
      
      // misses
      if (printMisses) {
        list = find.getMisses();
        if (list.size() == 1)
  	System.out.println("\nFound " + list.size() + " class that didn't match the criteria:\n");
        else
  	System.out.println("\nFound " + list.size() + " classes that didn't match the criteria:\n");
        for (i = 0; i < list.size(); i++)
  	System.out.println(list.get(i));
      }
    } 
    catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }
}

