import * as React from "react";
import "./Button.css";

export interface Props {
    intensity: number;
    selected: boolean;
}

const Button = ({ intensity, selected }: Props) =>
    <button className={`borg-button primary ${selected ? "selected" : ""}`}>{intensity}</button>;

export default Button;
