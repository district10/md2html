#!/usr/bin/perl

use 5.010;
use strict;

while (<>) {
    s/-<\r?\n?$/`@`{.fold}\n/;              # -<
    s/\+<\r?\n?$/`@`{.foldable}\n/;         # +<
    s/-<<\r?\n?$/`@`{.fold-ng}\n/;          # -<<
    s/\+<<\r?\n?$/`@`{.foldable-ng}\n/;     # +<<
    print;
}
