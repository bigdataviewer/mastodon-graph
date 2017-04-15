package org.mastodon.revisedundo.attributes;

public interface BeforeAttributeChangeListener< O >
{
	public void beforeAttributeChange( final Attribute< O > attribute, final O object );
}