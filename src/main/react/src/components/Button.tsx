import * as React from "react";
import "./Button.css";

export interface Props {
    intensity: number;
    selected: boolean;
}

const Button = ({ intensity, selected }: Props) => {
    return (
        <button className={`primary ${selected ? "borg-button-selected" : ""}`}>{intensity}</button>
    );
};

export default Button;
