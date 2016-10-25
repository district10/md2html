#!/usr/bin/perl

# @author: Tang Zhixiong
# @github: district10

use 5.010;
use strict;

sub inList {
    my $needle = shift;
    my @haystack = @_;
    foreach my $hay (@haystack) {
        if ( $needle eq $hay ) {
            return 1;
        }
    }
    return 0;
}

sub unfold {
    my($PADDING)  = shift @_;
    my($PREFIX)   = shift @_;
    my($FILENAME) = shift @_;

    if (&inList($FILENAME, @_) == 1) {
        return;
    }
    unshift(@_, $FILENAME);

    open my $INPUT, '<', $FILENAME;
    while(<$INPUT>) {
        if (/^(\s*)\@include <-(.*)=(.*)=/) {
            &unfold(
                     $PADDING . $1,
                     $PREFIX . $2,
                     $3, @_ );
        } else {
            print $PADDING . $PREFIX . $_;
        }
    }
}

&unfold("", "", $ARGV[0]);
