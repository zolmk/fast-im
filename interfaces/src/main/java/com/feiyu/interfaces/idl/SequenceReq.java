// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: interfaces.proto

// Protobuf Java Version: 3.25.0
package com.feiyu.interfaces.idl;

/**
 * Protobuf type {@code com.feiyu.interfaces.idl.SequenceReq}
 */
public final class SequenceReq extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.feiyu.interfaces.idl.SequenceReq)
    SequenceReqOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SequenceReq.newBuilder() to construct.
  private SequenceReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SequenceReq() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new SequenceReq();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_SequenceReq_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_SequenceReq_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.feiyu.interfaces.idl.SequenceReq.class, com.feiyu.interfaces.idl.SequenceReq.Builder.class);
  }

  public static final int UID_FIELD_NUMBER = 1;
  private long uid_ = 0L;
  /**
   * <code>int64 uid = 1;</code>
   * @return The uid.
   */
  @java.lang.Override
  public long getUid() {
    return uid_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (uid_ != 0L) {
      output.writeInt64(1, uid_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (uid_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, uid_);
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.feiyu.interfaces.idl.SequenceReq)) {
      return super.equals(obj);
    }
    com.feiyu.interfaces.idl.SequenceReq other = (com.feiyu.interfaces.idl.SequenceReq) obj;

    if (getUid()
        != other.getUid()) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + UID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getUid());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static com.feiyu.interfaces.idl.SequenceReq parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static com.feiyu.interfaces.idl.SequenceReq parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.feiyu.interfaces.idl.SequenceReq parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.feiyu.interfaces.idl.SequenceReq prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.feiyu.interfaces.idl.SequenceReq}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.feiyu.interfaces.idl.SequenceReq)
      com.feiyu.interfaces.idl.SequenceReqOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_SequenceReq_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_SequenceReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.feiyu.interfaces.idl.SequenceReq.class, com.feiyu.interfaces.idl.SequenceReq.Builder.class);
    }

    // Construct using com.feiyu.interfaces.idl.SequenceReq.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      uid_ = 0L;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_SequenceReq_descriptor;
    }

    @java.lang.Override
    public com.feiyu.interfaces.idl.SequenceReq getDefaultInstanceForType() {
      return com.feiyu.interfaces.idl.SequenceReq.getDefaultInstance();
    }

    @java.lang.Override
    public com.feiyu.interfaces.idl.SequenceReq build() {
      com.feiyu.interfaces.idl.SequenceReq result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.feiyu.interfaces.idl.SequenceReq buildPartial() {
      com.feiyu.interfaces.idl.SequenceReq result = new com.feiyu.interfaces.idl.SequenceReq(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.feiyu.interfaces.idl.SequenceReq result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.uid_ = uid_;
      }
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.feiyu.interfaces.idl.SequenceReq) {
        return mergeFrom((com.feiyu.interfaces.idl.SequenceReq)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.feiyu.interfaces.idl.SequenceReq other) {
      if (other == com.feiyu.interfaces.idl.SequenceReq.getDefaultInstance()) return this;
      if (other.getUid() != 0L) {
        setUid(other.getUid());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              uid_ = input.readInt64();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private long uid_ ;
    /**
     * <code>int64 uid = 1;</code>
     * @return The uid.
     */
    @java.lang.Override
    public long getUid() {
      return uid_;
    }
    /**
     * <code>int64 uid = 1;</code>
     * @param value The uid to set.
     * @return This builder for chaining.
     */
    public Builder setUid(long value) {

      uid_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int64 uid = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearUid() {
      bitField0_ = (bitField0_ & ~0x00000001);
      uid_ = 0L;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.feiyu.interfaces.idl.SequenceReq)
  }

  // @@protoc_insertion_point(class_scope:com.feiyu.interfaces.idl.SequenceReq)
  private static final com.feiyu.interfaces.idl.SequenceReq DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.feiyu.interfaces.idl.SequenceReq();
  }

  public static com.feiyu.interfaces.idl.SequenceReq getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SequenceReq>
      PARSER = new com.google.protobuf.AbstractParser<SequenceReq>() {
    @java.lang.Override
    public SequenceReq parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<SequenceReq> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SequenceReq> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.feiyu.interfaces.idl.SequenceReq getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

