/*
 * Copyright 2016 Aaron Knight
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.slkdev.swagger.confluence.cli;

import net.slkdev.swagger.confluence.config.SwaggerConfluenceConfig;
import net.slkdev.swagger.confluence.context.SwaggerConfluenceContextConfig;
import net.slkdev.swagger.confluence.exception.SwaggerConfluenceConfigurationException;
import net.slkdev.swagger.confluence.service.SwaggerToConfluenceService;
import org.apache.commons.cli.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.apache.commons.lang3.Validate.notNull;

public class SwaggerConfluence {

    private SwaggerToConfluenceService swaggerToConfluenceService;

    public SwaggerConfluence(final SwaggerToConfluenceService swaggerToConfluenceService){
        notNull(swaggerToConfluenceService, "SwaggerToConfluenceService Cannot Be Null!");
        this.swaggerToConfluenceService = swaggerToConfluenceService;
    }

    public static void main(final String[] args){
        final SwaggerToConfluenceService swaggerToConfluenceService = bootSwaggerConfluence();
        final SwaggerConfluence swaggerConfluence = new SwaggerConfluence(swaggerToConfluenceService);
        swaggerConfluence.runCLI(args);
    }

    private static SwaggerToConfluenceService bootSwaggerConfluence(){
        final AnnotationConfigApplicationContext annotationConfigApplicationContext =
                new AnnotationConfigApplicationContext(SwaggerConfluenceContextConfig.class);
        final SwaggerToConfluenceService swaggerToConfluenceService =
                annotationConfigApplicationContext.getBean(SwaggerToConfluenceService.class);
        annotationConfigApplicationContext.close();

        return swaggerToConfluenceService;
    }

    public void runCLI(final String[] args){
        final Options options = buildOptions();
        final CommandLine commandLine = parseCommandLineOptions(options, args);

        if(commandLine.hasOption("h") || args.length == 0){
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("swagger-confluence-cli", options);
        }
        else {
            final SwaggerConfluenceConfig swaggerConfluenceConfig =
                    buildSwaggerConfluenceConfig(commandLine);
            swaggerToConfluenceService.convertSwaggerToConfluence(swaggerConfluenceConfig);
        }
    }

    private static Options buildOptions(){
        final Options options = new Options();

        options.addOption("a", "ancestor-id", true, "ancestor id to use for the published api doc");
        options.addOption("b", "authentication", true, "base64 encoded user:pass pair for authentication");
        options.addOption("g", "generate-numeric-prefixes", true, "boolean flag to indicate whether to " +
                "generate numeric prefixes for titles");
        options.addOption("h", "help", false, "Print help message with usage information");
        options.addOption("i", "include-toc-on-single", true, "Include table of contents on single page mode");
        options.addOption("k", "space-key", true, "Space Key to publish api doc to");
        options.addOption("m", "pagination-mode", true, "Pagination mode to use: [single, category, individual]");
        options.addOption("s", "swagger-schema", true, "Swagger Schema name. Absolute, relative, or classpath location");
        options.addOption("p", "prefix", true, "Prefix to use for article titles to ensure uniqueness");
        options.addOption("t", "title", true, "Base title to use for the root article of the API doc");
        options.addOption("u", "confluence-rest-api-url", true, "URL to the confluence REST API");

        return options;
    }

    private static CommandLine parseCommandLineOptions(final Options options, final String[] args){
        final CommandLineParser commandLineParser = new DefaultParser();

        try {
            return commandLineParser.parse(options, args);

        } catch (ParseException e) {
            throw new SwaggerConfluenceConfigurationException(
                    "Error Parsing Command Line Arguments!", e);
        }
    }

    private static SwaggerConfluenceConfig buildSwaggerConfluenceConfig(final CommandLine commandLine){
        final SwaggerConfluenceConfig swaggerConfluenceConfig = new SwaggerConfluenceConfig();
        final String ancestorIdString = commandLine.getOptionValue("a");
        final Integer ancestorId;

        if(ancestorIdString == null){
            ancestorId = null;
        }
        else {
            ancestorId = Integer.valueOf(ancestorIdString);
        }

        swaggerConfluenceConfig.setAncestorId(ancestorId);
        swaggerConfluenceConfig.setAuthentication(commandLine.getOptionValue("b"));
        swaggerConfluenceConfig.setConfluenceRestApiUrl(commandLine.getOptionValue("u"));
        swaggerConfluenceConfig.setGenerateNumericPrefixes(
                Boolean.valueOf(commandLine.getOptionValue("g", "true"))
        );
        swaggerConfluenceConfig.setIncludeTableOfContentsOnSinglePage(
                Boolean.valueOf(commandLine.getOptionValue("i", "true"))
        );
        swaggerConfluenceConfig.setPaginationMode(commandLine.getOptionValue("m","single"));

        final String prefix = commandLine.getOptionValue("p");

        if(prefix != null){
            swaggerConfluenceConfig.setPrefix(prefix);
        }

        swaggerConfluenceConfig.setSpaceKey(commandLine.getOptionValue("k"));
        swaggerConfluenceConfig.setSwaggerSchema(commandLine.getOptionValue("s"));
        swaggerConfluenceConfig.setTitle(commandLine.getOptionValue("t"));

        return swaggerConfluenceConfig;
    }

}
