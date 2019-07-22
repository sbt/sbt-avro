package com.spotify.avro.mojo;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.apache.avro.compiler.specific.SpecificCompiler;
import org.apache.avro.generic.GenericData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AvscFilesCompiler {

  private static final Logger LOG = LoggerFactory.getLogger(AvscFilesCompiler.class);

  private Schema.Parser schemaParser;
  private String templateDirectory;
  private GenericData.StringType stringType;
  private SpecificCompiler.FieldVisibility fieldVisibility;
  private boolean useNamespace;
  private boolean enableDecimalLogicalType;
  private boolean createSetters;
  private Map<AvroFileRef, Exception> compileExceptions;
  private boolean logCompileExceptions;

  public AvscFilesCompiler(Schema.Parser schemaParser) {
    this.schemaParser = schemaParser;
//    schemaParser.setValidateDefaults(true);  // In avro 1.7.7
  }

  public void compileFiles(Set<AvroFileRef> files, File outputDirectory) {
    Set<AvroFileRef> compiledFiles = new HashSet<>();
    Set<AvroFileRef> uncompiledFiles = new HashSet<>(files);

    boolean progressed = true;
    while (progressed && !uncompiledFiles.isEmpty()) {
      progressed = false;
      compileExceptions = new HashMap<>();

      for (AvroFileRef file : uncompiledFiles) {
        boolean success = tryCompile(file, outputDirectory);
        if (success) {
          compiledFiles.add(file);
          progressed = true;
        }
      }

      uncompiledFiles.removeAll(compiledFiles);
    }

    if (!uncompiledFiles.isEmpty()) {
      String failedFiles = uncompiledFiles.stream()
          .map(AvroFileRef::toString)
          .collect(Collectors.joining(", "));
      SchemaGenerationException ex = new SchemaGenerationException(
          String.format("Can not compile schema files: %s", failedFiles));

      for (AvroFileRef file : uncompiledFiles) {
        Exception e = compileExceptions.get(file);
        if (e != null) {
          if (logCompileExceptions) {
            LOG.error(file.toString(), e);
          }
          ex.addSuppressed(e);
        }
      }
      throw ex;
    }
  }

  private boolean tryCompile(AvroFileRef src, File outputDirectory) {
    // on failure Schema.Parser changes cache state.
    // We want last successful state.
    Schema.Parser successfulSchemaParser = new Schema.Parser();
    successfulSchemaParser.addTypes(schemaParser.getTypes());
//    successfulSchemaParser.setValidateDefaults(schemaParser.getValidateDefaults());  // in avro 1.7.7

    try {
      Schema schema = schemaParser.parse(src.getFile());

      if (useNamespace) {
        if (schema.getType() != Schema.Type.RECORD && schema.getType() != Schema.Type.ENUM) {
          throw new SchemaGenerationException(String.format(
              "Error compiling schema file %s. "
                  + "Only one root RECORD or ENUM type is allowed per file.",
              src
          ));
        } else if (!src.pathToClassName().equals(schema.getFullName())) {
          throw new SchemaGenerationException(String.format(
              "Error compiling schema file %s. "
                  + "File class name %s does not match record class name %s",
              src,
              src.pathToClassName(),
              schema.getFullName()
          ));
        }
      }

      SpecificCompiler compiler = new SpecificCompiler(schema);
      compiler.setTemplateDir(templateDirectory);
      compiler.setStringType(stringType);
      compiler.setFieldVisibility(fieldVisibility);
      compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
      compiler.setCreateSetters(createSetters);
      compiler.compileToDestination(src.getFile(), outputDirectory);

    } catch (SchemaParseException e) {
      schemaParser = successfulSchemaParser;
      compileExceptions.put(src, e);
      return false;
    } catch (IOException e) {
      throw new SchemaGenerationException(
          String.format("Error compiling schema file %s to %s", src, outputDirectory), e);
    }

    return true;
  }

  public void setTemplateDirectory(String templateDirectory) {
    this.templateDirectory = templateDirectory;
  }

  public void setStringType(GenericData.StringType stringType) {
    this.stringType = stringType;
  }

  public void setFieldVisibility(SpecificCompiler.FieldVisibility fieldVisibility) {
    this.fieldVisibility = fieldVisibility;
  }

  public void setUseNamespace(boolean useNamespace) {
    this.useNamespace = useNamespace;
  }

  public void setEnableDecimalLogicalType(Boolean enableDecimalLogicalType) {
    this.enableDecimalLogicalType = enableDecimalLogicalType;
  }

  public void setCreateSetters(boolean createSetters) {
    this.createSetters = createSetters;
  }

  public void setLogCompileExceptions(final boolean logCompileExceptions) {
    this.logCompileExceptions = logCompileExceptions;
  }
}
