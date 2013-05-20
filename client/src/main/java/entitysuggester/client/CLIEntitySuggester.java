package entitysuggester.client;

import entitysuggester.client.recommender.CLIClientRecommender;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import net.myrrix.client.MyrrixClientConfiguration;
import org.apache.commons.cli.*;

/**
 *
 * @author Nilesh Chakraborty
 */
public class CLIEntitySuggester {

    public static class OptionComarator<T extends Option> implements Comparator<T> {

        private static final String OPTS_ORDER = "hpiracldbhostdbnamedbuserdbpass"; // option name order

        @Override
        public int compare(T o1, T o2) {
            String opt1, opt2;
            if ((opt1 = o1.getOpt()) == null) {
                opt1 = o1.getLongOpt();
            }
            if ((opt2 = o2.getOpt()) == null) {
                opt2 = o2.getLongOpt();
            }
            return OPTS_ORDER.indexOf(opt1 == null ? "" : opt1) - OPTS_ORDER.indexOf(opt2 == null ? "" : opt2);
        }
    }

    public static class CustomParser extends GnuParser {

        @Override
        public CommandLine parse(Options options, String[] arguments) throws ParseException {
            CommandLine cmd = super.parse(options, arguments);
            checkRequiredOptions();
            return cmd;
        }

        @Override
        protected void processOption(String arg, ListIterator iter) throws ParseException {
            if (getOptions().hasOption(arg)) {
                super.processOption(arg, iter);
            }
        }
    }

    private static void parseCommandLine(String... args) {
        OptionGroup optionGroup1 = new OptionGroup();
        optionGroup1.addOption(OptionBuilder.hasArg().isRequired().withArgName("port").withDescription("Myrrix serving layer port").withLongOpt("port").create('p'));
        optionGroup1.addOption(OptionBuilder.hasArg().isRequired().withArgName("hostname/IP").withDescription("Myrrix serving layer host").withLongOpt("host").create('h'));
        optionGroup1.addOption(OptionBuilder.hasArg().withArgName("CSV file name").withDescription("Ingest CSV file").withLongOpt("ingest").create('i'));

        OptionGroup optionGroup2 = new OptionGroup();
        optionGroup2.addOption(OptionBuilder.hasArg().isRequired().withArgName("property list file").withDescription("File with list of properties and property----value pairs").withLongOpt("property-list").create('l'));
        optionGroup2.addOption(OptionBuilder.hasArg().withArgName("how many").withDescription("Number of recommendations to fetch").withLongOpt("count").create('c'));
        optionGroup2.addOption(OptionBuilder.hasArg().isRequired().withArgName("MySQL Database Host").withLongOpt("dbhost").create());
        optionGroup2.addOption(OptionBuilder.hasArg().isRequired().withArgName("MySQL Database Name").withLongOpt("dbname").create());
        optionGroup2.addOption(OptionBuilder.hasArg().isRequired().withArgName("MySQL Database user").withLongOpt("dbuser").create());
        optionGroup2.addOption(OptionBuilder.hasArg().isRequired().withArgName("MySQL Database Password").withLongOpt("dbpass").create());

        OptionGroup optionGroup3 = new OptionGroup();
        optionGroup3.addOption(OptionBuilder.hasArgs(2).withArgName("item ID> <property|value").withDescription("Recommend properties or property----value pairs for item with given id. Type of recommendation can be either 'property' or 'value'").withLongOpt("recommend").create('r'));
        optionGroup3.addOption(OptionBuilder.hasArgs().withArgName("property|value> <p1> [<p2> ...] [<p1----v1> <p2----v2> ...]").withDescription("Recommend properties/values for an 'anonymous' item. A list of properties and/or property:value pairs is given as input.").withLongOpt("recommend-anon").create('a'));
        optionGroup3.setRequired(true);

        if (args.length == 0) {
            Options options = new Options();
            options.addOptionGroup(optionGroup1);
            options.addOptionGroup(optionGroup2);
            options.addOptionGroup(optionGroup3);
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setOptionComparator(new OptionComarator());
            helpFormatter.setDescPadding(2);
            String jarName = new java.io.File(CLIEntitySuggester.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            helpFormatter.printHelp(120, "java -jar " + jarName, "\ndetailed usage:\n\n", options, "Thanks for using the entity-suggester prototype. Remember to start the Myrrix instance before running this.", true);
        } else {
            CommandLine cmd;
            try {
                Options options = new Options();
                for (Iterator it = optionGroup1.getOptions().iterator(); it.hasNext();) {
                    Option option = (Option) it.next();
                    options.addOption(option);
                }
                cmd = new CustomParser().parse(options, args);

                MyrrixClientConfiguration config = new MyrrixClientConfiguration();

                if (cmd.hasOption('p')) {
                    config.setPort(Integer.parseInt(cmd.getOptionValue('p')));
                }
                if (cmd.hasOption('h')) {
                    config.setHost(cmd.getOptionValue('h'));
                }
                if (cmd.hasOption('i')) {
                    CLIClientRecommender customClientRecommender = new CLIClientRecommender(config);
                    customClientRecommender.ingest(cmd.getOptionValue('i'));
                } else {
                    options = new Options();
                    for (Iterator it = optionGroup2.getOptions().iterator(); it.hasNext();) {
                        Option option = (Option) it.next();
                        options.addOption(option);
                    }
                    options.addOptionGroup(optionGroup3);
                    cmd = new CustomParser().parse(options, args);

                    String recommendTo, recommendType;

                    String idListFile = cmd.getOptionValue('l');
                    int howMany;
                    if (cmd.hasOption('c')) {
                        howMany = Integer.parseInt(cmd.getOptionValue('c'));
                    } else {
                        howMany = 10;
                    }

                    CLIClientRecommender customClientRecommender = new CLIClientRecommender(config);
                    customClientRecommender.setDatabaseInfo(cmd.getOptionValue("dbhost"), cmd.getOptionValue("dbname"), cmd.getOptionValue("dbuser"), cmd.getOptionValue("dbpass"));

                    if (cmd.hasOption('r')) {
                        if (cmd.getOptionValues('r').length < 2) {
                            throw new MissingArgumentException(new Option("r", "Recommend properties or property----value pairs for item with given id. Type of recommendation can be either 'property' or 'value'"));
                        }
                        recommendTo = cmd.getOptionValues('r')[0];
                        recommendType = cmd.getOptionValues('r')[1];
                        customClientRecommender.recommend(idListFile, recommendTo, recommendType, howMany);
                    } else {
                        if (cmd.getOptionValues('a').length < 2 || (!cmd.getOptionValues('a')[0].equals("property") && !cmd.getOptionValues('a')[0].equals("value"))) {
                            throw new MissingArgumentException(new Option("a", "Recommend properties/values for an 'anonymous' item. A list of properties and/or property----value pairs is given as input."));
                        }
                        recommendType = cmd.getOptionValues('a')[0];
                        String[] arg = cmd.getOptionValues('a');
                        String[] list = Arrays.copyOfRange(arg, 1, arg.length);
                        customClientRecommender.recommendAnonymous(idListFile, recommendType, howMany, list);
                    }
                }
            } catch (MissingOptionException ex) {
                System.out.print("Options missing : ");
                for (Iterator it = ex.getMissingOptions().iterator(); it.hasNext();) {
                    Object o = it.next();
                    if (o instanceof OptionGroup) {
                        OptionGroup og = (OptionGroup) o;
                        for (Iterator itr = og.getOptions().iterator(); itr.hasNext();) {
                            Option option = (Option) itr.next();
                            System.out.print("-" + option.getOpt() + " ");
                        }
                    } else {
                        System.out.print("-" + o + " ");
                    }
                }
                System.out.println();
            } catch (MissingArgumentException ex) {
                Option missingOption = ex.getOption();
                System.out.println("Argument missing for : -" + missingOption.getOpt());
            } catch (ParseException ex) {
                System.out.println("Error parsing arguments - " + ex.getMessage());
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        parseCommandLine(args);
    }
}
